package com.assets.board.service;

import com.assets.board.model.ib.DividendRecord;
import com.assets.board.model.ib.IBPosition;
import com.assets.board.model.ib.TradeRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class IBFilesParser {

    private static final DateTimeFormatter IB_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Data
    public static class DividendData {
        private String symbol;
        private LocalDate date;
        private BigDecimal amount;
    }

    @Data
    public static class TradeData {
        private String symbol;
        private String description;
        private String currency;
        private String side;
        private LocalDate tradeDate;
        private LocalDate openDate;
        private BigDecimal quantity;
        private BigDecimal tradePrice;
        private BigDecimal fifoPnlRealized;
        private BigDecimal proceeds;
        private BigDecimal ibCommission;
        private BigDecimal pnl;
    }

    @Data
    public static class WithholdingTaxData {
        private String symbol;
        private LocalDate date;
        private BigDecimal amount;
    }

    @Data
    public static class ParsedData {
        private List<DividendData> dividends = new ArrayList<>();
        private List<TradeData> trades = new ArrayList<>();
        private List<WithholdingTaxData> withholdingTaxes = new ArrayList<>();
    }

    /**
     * All section headers start with "ClientAccountID". Sections are identified by
     * unique column names: trades contain "TradeDate", dividends contain "DividendType".
     */
    public ParsedData parseIBFile(MultipartFile ibReportFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ibReportFile.getInputStream()))) {
            List<String> tradeLines = new ArrayList<>();
            List<String> dividendLines = new ArrayList<>();
            String currentSection = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                if (line.startsWith("\"ClientAccountID\"")) {
                    if (line.contains("\"TradeDate\"")) {
                        currentSection = "TRADES";
                        tradeLines.add(line);
                    } else if (line.contains("\"DividendType\"")) {
                        currentSection = "DIVIDENDS";
                        dividendLines.add(line);
                    } else {
                        currentSection = null;
                    }
                } else if ("TRADES".equals(currentSection)) {
                    tradeLines.add(line);
                } else if ("DIVIDENDS".equals(currentSection)) {
                    dividendLines.add(line);
                }
            }

            ParsedData result = new ParsedData();
            result.setDividends(parseDividends(dividendLines));
            result.setTrades(parseTrades(tradeLines));
            return result;
        } catch (Exception e) {
            log.error("Failed to parse IB file", e);
            return new ParsedData();
        }
    }

    public List<IBPosition> parseIBPositions(MultipartFile ibReportFile) {
        try (Reader reader = new InputStreamReader(ibReportFile.getInputStream())) {
            CsvToBean<IBPosition> csvToBean = new CsvToBeanBuilder<IBPosition>(reader)
                    .withType(IBPosition.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (Exception e) {
            log.error("Failed to parse IB positions file", e);
            return Collections.emptyList();
        }
    }

    private List<DividendData> parseDividends(List<String> lines) {
        if (lines.size() <= 1) return new ArrayList<>();

        try (StringReader reader = new StringReader(String.join("\n", lines))) {
            CsvToBean<DividendRecord> csvToBean = new CsvToBeanBuilder<DividendRecord>(reader)
                    .withType(DividendRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse().stream()
                    .filter(r -> "Dividends".equals(r.getType()))
                    .map(r -> {
                        DividendData d = new DividendData();
                        d.setSymbol(r.getSymbol());
                        d.setDate(LocalDate.parse(r.getDateTime().split(";")[0], IB_DATE));
                        d.setAmount(r.getAmount());
                        return d;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing dividends", e);
        }
    }

    private List<TradeData> parseTrades(List<String> lines) {
        if (lines.size() <= 1) return new ArrayList<>();

        try (StringReader reader = new StringReader(String.join("\n", lines))) {
            CsvToBean<TradeRecord> csvToBean = new CsvToBeanBuilder<TradeRecord>(reader)
                    .withType(TradeRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse().stream()
                    .filter(r -> "STK".equals(r.getAssetClass()))
                    .filter(r -> "SELL".equals(r.getSide()))
                    .filter(r -> "CLOSED_LOT".equals(r.getLevelOfDetail()))
                    .map(r -> {
                        TradeData t = new TradeData();
                        t.setSymbol(r.getSymbol());
                        t.setDescription(r.getDescription());
                        t.setCurrency(r.getCurrency());
                        t.setSide(r.getSide());
                        t.setTradeDate(LocalDate.parse(r.getTradeDate(), IB_DATE));

                        if (r.getOpenDateTime() != null && !r.getOpenDateTime().isBlank()) {
                            t.setOpenDate(LocalDate.parse(r.getOpenDateTime().split(";")[0], IB_DATE));
                        }

                        t.setQuantity(r.getQuantity().abs());
                        t.setTradePrice(r.getTradePrice());
                        t.setFifoPnlRealized(r.getFifoPnlRealized());

                        // Proceeds is empty in CLOSED_LOT records — derive from price × qty
                        if (r.getProceeds() != null && r.getProceeds().compareTo(BigDecimal.ZERO) != 0) {
                            t.setProceeds(r.getProceeds().abs());
                        } else {
                            t.setProceeds(r.getFifoPnlRealized().multiply(r.getQuantity().abs()));
                        }

                        t.setIbCommission(r.getIbCommission() != null ? r.getIbCommission().abs() : BigDecimal.ZERO);
                        return t;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing trades", e);
        }
    }
}

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
        private LocalDate date;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal netCash;
        private String side;
        private String currency;
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
     * Parses the IB "Dividends and Trades" CSV export.
     *
     * The file contains two back-to-back sections with different headers:
     *   - Trades:    first column = "TradeDate"
     *   - Dividends: first column = "Date/Time"
     */
    public ParsedData parseIBFile(MultipartFile ibReportFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ibReportFile.getInputStream()))) {
            List<String> tradeLines = new ArrayList<>();
            List<String> dividendLines = new ArrayList<>();
            String currentSection = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                if (line.startsWith("\"TradeDate\"")) {
                    currentSection = "TRADES";
                    tradeLines.add(line);
                } else if (line.startsWith("\"Date/Time\"")) {
                    currentSection = "DIVIDENDS";
                    dividendLines.add(line);
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
        try {
            try (Reader reader = new InputStreamReader(ibReportFile.getInputStream())) {
                CsvToBean<IBPosition> csvToBean = new CsvToBeanBuilder<IBPosition>(reader)
                        .withType(IBPosition.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                return csvToBean.parse();
            }
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
                        // Date/Time column format: "yyyyMMdd;HHmmss"
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
                    .map(r -> {
                        TradeData t = new TradeData();
                        t.setSymbol(r.getSymbol());
                        t.setDate(LocalDate.parse(r.getTradeDate(), IB_DATE));
                        t.setQuantity(r.getQuantity());
                        t.setPrice(r.getTradePrice());
                        t.setNetCash(r.getNetCash());
                        t.setSide(r.getSide());
                        t.setCurrency(r.getCurrency());
                        t.setPnl(r.getPnl());
                        return t;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing trades", e);
        }
    }
}

package com.assets.board.service;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.assets.board.model.ib.DividendRecord;
import com.assets.board.model.ib.IBRecord;
import com.assets.board.model.ib.WithholdingTaxRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class IBDividendParser {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^([A-Z]+)(?=\\()");

    @Data
    public static class DividendData {
        private String symbol;
        private LocalDate date;
        private BigDecimal amount;
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
        private List<WithholdingTaxData> withholdingTaxes = new ArrayList<>();
    }

    public ParsedData parseFile(Reader reader) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<String> dividendLines = new ArrayList<>();
        List<String> withholdingTaxLines = new ArrayList<>();

        String line;
        String currentSection = null;

        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(",", 2);

            if (parts.length > 0) {
                String firstColumn = parts[0].trim();

                // Check if this is a header line
                if (line.contains(",Header,")) {
                    currentSection = firstColumn;
                }
                // Check if this is a data line
                else if (line.contains(",Data,")) {
                    if ("Dividends".equals(currentSection)) {
                        dividendLines.add(line);
                    } else if ("Withholding Tax".equals(currentSection)) {
                        withholdingTaxLines.add(line);
                    }
                }
            }
        }

        ParsedData result = new ParsedData();
        result.setDividends(parseDividends(dividendLines));
        result.setWithholdingTaxes(parseWithholdingTaxes(withholdingTaxLines));

        return result;
    }

    private List<DividendData> parseDividends(List<String> lines) {
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }

        String csv = String.join("\n", lines);

        try (StringReader stringReader = new StringReader(csv)) {
            CsvToBean<DividendRecord> csvToBean = new CsvToBeanBuilder<DividendRecord>(stringReader)
                    .withType(DividendRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse().stream()
                    .filter(r -> !r.getDate().isBlank())
                    .map(r -> {
                        DividendData d = new DividendData();
                        d.setSymbol(extractSymbol(r.getDescription()));
                        d.setDate(LocalDate.parse(r.getDate()));
                        d.setAmount(r.getAmount());
                        return d;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing dividends", e);
        }
    }

    private List<WithholdingTaxData> parseWithholdingTaxes(List<String> lines) {
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }

        String csv = String.join("\n", lines);

        try (StringReader stringReader = new StringReader(csv)) {
            CsvToBean<WithholdingTaxRecord> csvToBean = new CsvToBeanBuilder<WithholdingTaxRecord>(stringReader)
                    .withType(WithholdingTaxRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse().stream()
                    .filter(r -> !r.getDate().isBlank())
                    .map(r -> {
                        WithholdingTaxData w = new WithholdingTaxData();
                        w.setSymbol(extractSymbol(r.getDescription()));
                        w.setDate(LocalDate.parse(r.getDate()));
                        w.setAmount(r.getAmount());
                        return w;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing withholding taxes", e);
        }
    }

    private String extractSymbol(String description) {
        if (description == null) return null;
        Matcher matcher = SYMBOL_PATTERN.matcher(description);
        return matcher.find() ? matcher.group(1) : null;
    }
}

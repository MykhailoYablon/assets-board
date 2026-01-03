package com.assets.board.model.ib;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class IBRecord {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^([A-Z]+)(?=\\()");

    @CsvBindByPosition(position = 0)
    private String recordType;

    @CsvBindByPosition(position = 1)
    private String dataType;

    @CsvBindByPosition(position = 3)
    private String date;

    @CsvBindByPosition(position = 4)
    private String description;

    @CsvBindByPosition(position = 5)
    private BigDecimal amount;

    public String getSymbol() {
        if (description == null) return null;
        Matcher matcher = SYMBOL_PATTERN.matcher(description);
        return matcher.find() ? matcher.group(1) : null;
    }

    public LocalDate getDateAsLocalDate() {
        return date != null ? LocalDate.parse(date) : null;
    }

    public boolean isDividend() {
        return "Dividends".equals(recordType) && "Data".equals(dataType) && !"Total".equals(date);
    }

    public boolean isWithholdingTax() {
        return "Withholding Tax".equals(recordType) && "Data".equals(dataType);
    }
}

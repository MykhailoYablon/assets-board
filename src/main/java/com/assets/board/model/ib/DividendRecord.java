package com.assets.board.model.ib;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DividendRecord {

    @CsvBindByName(column = "Date/Time")
    private String dateTime;

    @CsvBindByName(column = "Symbol")
    private String symbol;

    @CsvBindByName(column = "Amount")
    private BigDecimal amount;

    @CsvBindByName(column = "Type")
    private String type;
}

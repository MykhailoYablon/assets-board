package com.assets.board.model.ib;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DividendRecord {

    @CsvBindByPosition(position = 3)
    private String date;

    @CsvBindByPosition(position = 4)
    private String description;

    @CsvBindByPosition(position = 5)
    private BigDecimal amount;

    @JsonProperty("nbu")
    String nbu;

    @JsonProperty("uaBrutto")
    Double uaBrutto;

    @JsonProperty("tax9")
    Double tax9;

    @JsonProperty("militaryTax5")
    Double militaryTax5;

    @JsonProperty("taxSum")
    Double taxSum;

//    USTaxUSD,USTaxUAH,TotalTaxUAH,USNetto,UANetto,Dividends$Netto
}

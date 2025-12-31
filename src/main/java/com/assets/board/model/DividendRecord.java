package com.assets.board.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.Setter;

@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DividendRecord {

    @JsonProperty("symbol")
    String symbol;

    @CsvBindByName(column = "Date")
    @JsonProperty("date")
    String date;

    @CsvBindByName(column = "Currency")
    @JsonProperty("currency")
    String currency;

    @CsvBindByName(column = "Description")
    @JsonProperty("description")
    String description;

    @CsvBindByName(column = "Amount")
    @JsonProperty("amount")
    String amount;

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
}

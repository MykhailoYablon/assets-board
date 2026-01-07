package com.assets.board.model.ib;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DividendTaxReport {

    String symbol;

    private LocalDate date;

    private BigDecimal amount;

    @JsonProperty("nbuRate")
    BigDecimal nbuRate;

    @JsonProperty("uaBrutto")
    BigDecimal uaBrutto;

    @JsonProperty("tax9")
    BigDecimal tax9;

    @JsonProperty("militaryTax5")
    BigDecimal militaryTax5;

    @JsonProperty("taxSum")
    BigDecimal taxSum;

    @JsonProperty("usTaxUSD")
    BigDecimal usTaxUSD;

    @JsonProperty("usTaxUAH")
    BigDecimal usTaxUAH;

    @JsonProperty("totalTaxUAH")
    BigDecimal totalTaxUAH;

    @JsonProperty("usNetto")
    BigDecimal usNetto;

    @JsonProperty("uaNetto")
    BigDecimal uaNetto;

    @JsonProperty("dividends$Netto")
    BigDecimal dividends$Netto;

}

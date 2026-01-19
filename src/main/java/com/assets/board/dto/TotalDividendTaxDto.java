package com.assets.board.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalDividendTaxDto {
    BigDecimal totalAmount;
    BigDecimal totalUaBrutto;
    BigDecimal totalTax9;
    BigDecimal totalMilitaryTax5;
    BigDecimal totalTaxSum;
//    BigDecimal totalUsTaxUSD;
//    BigDecimal totalUsTaxUAH;
//    BigDecimal totalTaxUAH;
//    BigDecimal totalUsNetto;
//    BigDecimal totalUaNetto;
//    BigDecimal totalDividendsNetto;
}

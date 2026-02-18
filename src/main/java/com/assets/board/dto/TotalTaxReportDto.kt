package com.assets.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TotalTaxReportDto {

    Short year;
    BigDecimal totalAmount;
    BigDecimal totalUaBrutto;
    BigDecimal totalTax9;
    BigDecimal totalMilitaryTax5;
    BigDecimal totalTaxSum;
    List<DividendTaxReportDto> taxReportDtos;

//    BigDecimal totalUsTaxUSD;
//    BigDecimal totalUsTaxUAH;
//    BigDecimal totalTaxUAH;
//    BigDecimal totalUsNetto;
//    BigDecimal totalUaNetto;
//    BigDecimal totalDividendsNetto;
}

package com.assets.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SymbolDividendSummaryDto {

    String symbol;
    BigDecimal totalAmount;
    BigDecimal totalUaBrutto;
    BigDecimal totalTax9;
    BigDecimal totalMilitaryTax5;
    BigDecimal totalTaxSum;
    List<DividendTaxReportDto> payments;
}

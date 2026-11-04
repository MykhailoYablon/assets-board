package com.assets.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SymbolTradeSummaryDto {

    String symbol;
    BigDecimal totalUaBrutto;
    BigDecimal totalTax;
    BigDecimal totalMilitary;
    BigDecimal totalTaxSum;
    List<TradeTaxReportDto> trades;
}

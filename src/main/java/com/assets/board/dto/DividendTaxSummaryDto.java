package com.assets.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DividendTaxSummaryDto {

    Short year;
    BigDecimal totalAmount;
    BigDecimal totalUaBrutto;
    BigDecimal totalTax9;
    BigDecimal totalMilitaryTax5;
    BigDecimal totalTaxSum;
    List<SymbolDividendSummaryDto> bySymbol;
}

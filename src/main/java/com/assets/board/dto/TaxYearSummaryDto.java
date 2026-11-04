package com.assets.board.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TaxYearSummaryDto {

    Short year;

    // Dividend totals
    BigDecimal dividendAmount;
    BigDecimal dividendUaBrutto;
    BigDecimal dividendTax;
    BigDecimal dividendMilitary;
    BigDecimal dividendTaxSum;

    // Trade totals
    BigDecimal tradeUaBrutto;
    BigDecimal tradeTax;
    BigDecimal tradeMilitary;
    BigDecimal tradeTaxSum;

    // Combined
    BigDecimal totalTaxSum;
    BigDecimal withheldDividendTax;

    List<SymbolDividendSummaryDto> dividendsBySymbol;
    List<SymbolTradeSummaryDto> tradesBySymbol;
}

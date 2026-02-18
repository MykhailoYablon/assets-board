package com.assets.board.dto

import java.math.BigDecimal

data class TotalTaxReportDto (
    var year: Short? = null,
    var totalAmount: BigDecimal? = null,
    var totalUaBrutto: BigDecimal? = null,
    var totalTax9: BigDecimal? = null,
    var totalMilitaryTax5: BigDecimal? = null,
    var totalTaxSum: BigDecimal? = null,
    var taxReportDtos: MutableList<DividendTaxReportDto?>? = null
)

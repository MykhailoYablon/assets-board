package com.assets.board.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate

data class DividendTaxReportDto (
    var symbol: String? = null,

    var date: LocalDate? = null,

    var amount: BigDecimal? = null,

    @field:JsonProperty("nbuRate")
    var nbuRate: BigDecimal? = null,

    @field:JsonProperty("uaBrutto")
    var uaBrutto: BigDecimal? = null,

    @field:JsonProperty("tax9")
    var tax9: BigDecimal? = null,

    @field:JsonProperty("militaryTax5")
    var militaryTax5: BigDecimal? = null,

    @field:JsonProperty("taxSum")
    var taxSum: BigDecimal? = null
)

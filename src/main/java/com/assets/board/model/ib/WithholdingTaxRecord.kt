package com.assets.board.model.ib

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.opencsv.bean.CsvBindByPosition
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class WithholdingTaxRecord (
    @JsonProperty("symbol")
    var symbol: String? = null,

    @CsvBindByPosition(position = 3)
    val date: String? = null,

    @CsvBindByPosition(position = 4)
    val description: String? = null,

    @CsvBindByPosition(position = 5)
    val amount: BigDecimal? = null
)
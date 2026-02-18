package com.assets.board.model.ib

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.opencsv.bean.CsvBindByPosition
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class DividendRecord(
    @CsvBindByPosition(position = 3)
    val date: String? = null,

    @CsvBindByPosition(position = 4)
    val description: String? = null,

    @CsvBindByPosition(position = 5)
    val amount: BigDecimal? = null
)

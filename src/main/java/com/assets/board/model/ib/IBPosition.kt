package com.assets.board.model.ib

import com.opencsv.bean.CsvBindByName
import lombok.Data

@Data
data class IBPosition (
    private val id: Long? = null,

    @CsvBindByName(column = "Symbol")
    private val symbol: String? = null,

    @CsvBindByName(column = "Description")
    private val description: String? = null,

    @CsvBindByName(column = "Quantity")
    private val quantity: Int? = null,

    @CsvBindByName(column = "MarkPrice")
    private val markPrice: Double? = null,

    @CsvBindByName(column = "PositionValue")
    private val positionValue: Double? = null,

    @CsvBindByName(column = "OpenPrice")
    private val openPrice: Double? = null,

    @CsvBindByName(column = "CostBasisPrice")
    private val costBasisPrice: Double? = null,

    @CsvBindByName(column = "FifoPnlUnrealized")
    private val fifoPnlUnrealized: Double? = null,

    @CsvBindByName(column = "HoldingPeriodDateTime")
    private val holdingPeriodDateTime: String? = null,

    @CsvBindByName(column = "Weight")
    private val weight: Double? = null
)

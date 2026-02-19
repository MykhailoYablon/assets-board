package com.assets.board.entity

import jakarta.persistence.*
import lombok.Data

@Entity
@Table(name = "positions")
data class Position (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "symbol")
    var symbol: String? = null,

    @Column(name = "description", length = 500)
    var description: String? = null,

    @Column(name = "quantity")
    var quantity: Int? = null,

    @Column(name = "mark_price")
    var markPrice: Double? = null,

    @Column(name = "position_value")
    var positionValue: Double? = null,

    @Column(name = "open_price")
    var openPrice: Double? = null,

    @Column(name = "cost_basis_price")
    var costBasisPrice: Double? = null,

    @Column(name = "fifo_pnl_unrealized")
    var fifoPnlUnrealized: Double? = null,

    @Column(name = "holding_period_date_time")
    var holdingPeriodDateTime: String? = null,

    @Column(name = "weight")
    var weight: Double? = null
)

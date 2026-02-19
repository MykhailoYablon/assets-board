package com.assets.board.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "exchange_rates", uniqueConstraints = [UniqueConstraint(columnNames = ["date"])])
data class ExchangeRate (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var date: LocalDate? = null,

    @Column(name = "nbu_rate", nullable = false, precision = 10, scale = 4)
    var nbuRate: BigDecimal? = null,

    @Column(nullable = false)
    var currency: String? = "USD"
)
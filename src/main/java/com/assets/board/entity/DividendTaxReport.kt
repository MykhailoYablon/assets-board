package com.assets.board.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "dividend_tax_report")
open class DividendTaxReport(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "total_tax_report_id", nullable = false)
    var totalTaxReport: TotalTaxReport? = null,

    @Column(nullable = false)
    var symbol: String? = null,

    @Column(nullable = false)
    var date: LocalDate? = null,

    @Column(nullable = false, precision = 10, scale = 4)
    var amount: BigDecimal? = null,

    @Column(name = "nbu_rate", nullable = false, precision = 10, scale = 4)
    var nbuRate: BigDecimal? = null,

    @Column(name = "ua_brutto", nullable = false, precision = 10, scale = 4)
    var uaBrutto: BigDecimal? = null,

    @Column(name = "tax_9", nullable = false, precision = 10, scale = 4)
    var tax9: BigDecimal? = null,

    @Column(name = "military_tax_5", nullable = false, precision = 10, scale = 4)
    var militaryTax5: BigDecimal? = null,

    @Column(name = "tax_sum", nullable = false, precision = 10, scale = 4)
    var taxSum: BigDecimal? = null
)

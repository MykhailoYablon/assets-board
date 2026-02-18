package com.assets.board.entity

import com.assets.board.model.enums.ReportStatus
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import lombok.Data
import java.math.BigDecimal
import java.util.function.Consumer

@Entity
@Table(name = "total_tax_report")
@Data
open class TotalTaxReport (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var year: Short? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReportStatus? = null,

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    var totalAmount: BigDecimal? = null,

    @Column(name = "total_ua_brutto", nullable = false, precision = 19, scale = 2)
    var totalUaBrutto: BigDecimal? = null,

    @Column(name = "total_tax_9", nullable = false, precision = 19, scale = 2)
    var totalTax9: BigDecimal? = null,

    @Column(name = "total_military_tax_5", nullable = false, precision = 19, scale = 2)
    var totalMilitaryTax5: BigDecimal? = null,

    @Column(name = "total_tax_sum", nullable = false, precision = 19, scale = 2)
    var totalTaxSum: BigDecimal? = null,

    @OneToMany(mappedBy = "totalTaxReport", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    var taxReports: MutableList<DividendTaxReport?> = ArrayList()
) {

    fun addTaxReport(report: DividendTaxReport) {
        taxReports.add(report)
        report.totalTaxReport = this
    }

    // Add this method for clearing
    fun clearTaxReports() {
        this.taxReports.clear()
    }
}

package com.assets.board.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dividend_tax_report")
@Data
public class DividendTaxReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "total_tax_report_id", nullable = false)
    TotalTaxReport totalTaxReport;

    @Column(nullable = false)
    String symbol;

    @Column(nullable = false)
    LocalDate date;

    @Column(nullable = false, precision = 2, scale = 2)
    BigDecimal amount;

    @Column(name = "nbu_rate", nullable = false, precision = 10, scale = 4)
    BigDecimal nbuRate;

    @Column(name = "ua_brutto", nullable = false, precision = 2, scale = 2)
    BigDecimal uaBrutto;

    @Column(name = "tax_9", nullable = false, precision = 2, scale = 2)
    BigDecimal tax9;

    @Column(name = "military_tax_5", nullable = false, precision = 2, scale = 2)
    BigDecimal militaryTax5;

    @Column(name = "tax_sum", nullable = false, precision = 2, scale = 2)
    BigDecimal taxSum;

}

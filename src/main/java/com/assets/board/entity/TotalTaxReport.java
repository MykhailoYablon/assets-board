package com.assets.board.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "total_tax_report")
@Data
public class TotalTaxReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "total_ua_brutto", nullable = false, precision = 19, scale = 2)
    BigDecimal totalUaBrutto;

    @Column(name = "total_tax_9", nullable = false, precision = 19, scale = 2)
    BigDecimal totalTax9;

    @Column(name = "total_military_tax_5", nullable = false, precision = 19, scale = 2)
    BigDecimal totalMilitaryTax5;

    @Column(name = "total_tax_sum", nullable = false, precision = 19, scale = 2)
    BigDecimal totalTaxSum;

    @OneToMany(mappedBy = "totalTaxReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<DividendTaxReport> taxReports;
}

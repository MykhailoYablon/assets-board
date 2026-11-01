package com.assets.board.entity;

import com.assets.board.model.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "total_tax_report")
@Data
public class TotalTaxReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Short year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(name = "trade_total_gain", nullable = false, precision = 19, scale = 2)
    BigDecimal tradeTotalGain;

  @Column(name = "dividend_total_gain", nullable = false, precision = 19, scale = 2)
    BigDecimal dividendTotalGain;

    @Column(name = "trade_total_ua_brutto", nullable = false, precision = 19, scale = 2)
    BigDecimal tradeTotalUaBrutto;

    @Column(name = "dividend_total_ua_brutto", nullable = false, precision = 19, scale = 2)
    BigDecimal dividendTotalUaBrutto;

    @Column(name = "dividend_total_tax", nullable = false, precision = 19, scale = 2)
    BigDecimal dividendTotalTax;

    @Column(name = "trade_total_tax", nullable = false, precision = 19, scale = 2)
    BigDecimal tradeTotalTax;

    @Column(name = "trade_military_tax_5", nullable = false, precision = 19, scale = 2)
    BigDecimal tradeTotalMilitaryTax5;

    @Column(name = "dividend_military_tax_5", nullable = false, precision = 19, scale = 2)
    BigDecimal dividendTotalMilitaryTax5;

    @Column(name = "total_military_tax_5", nullable = false, precision = 19, scale = 2)
    BigDecimal totalMilitaryTax5;

    @Column(name = "total_tax_sum", nullable = false, precision = 19, scale = 2)
    BigDecimal totalTaxSum;

    @OneToMany(mappedBy = "totalTaxReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<DividendTaxReport> dividendTaxReports = new ArrayList<>();

    @OneToMany(mappedBy = "totalTaxReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    List<TradeTaxReport> tradeTaxReports = new ArrayList<>();

  public void setTaxReports(List<DividendTaxReport> dividendTaxReports, List<TradeTaxReport>  tradeTaxReports) {
    this.dividendTaxReports.clear();
    this.tradeTaxReports.clear();
    if (dividendTaxReports != null) {
      dividendTaxReports.forEach(this::addDividendTaxReport);  // Add new ones
    }
    if (tradeTaxReports != null) {
      tradeTaxReports.forEach(this::addTradeTaxReport);
    }
  }

    public void addDividendTaxReport(DividendTaxReport report) {
      dividendTaxReports.add(report);
      report.setTotalTaxReport(this);
    }

    public void addTradeTaxReport(TradeTaxReport report) {
      tradeTaxReports.add(report);
      report.setTotalTaxReport(this);
    }

    // Add this method for clearing
    public void clearTaxReports() {
      this.dividendTaxReports.clear();
      this.tradeTaxReports.clear();
    }
}

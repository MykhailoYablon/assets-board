package com.assets.board.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trade_tax_report")
@Data
public class TradeTaxReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "total_tax_report_id", nullable = false)
  TotalTaxReport totalTaxReport;

  @Column(nullable = false)
  String symbol;

  @Column(nullable = false, precision = 10, scale = 4)
  BigDecimal quantity;

  @Column(nullable = false)
  LocalDate buyDate;

  @Column(nullable = false)
  LocalDate sellDate;

  @Column(nullable = false, name = "original_price")
  BigDecimal originalPrice;

  @Column(nullable = false, name = "sell_price")
  BigDecimal sellPrice;

  @Column(name = "nbu_rate_buy", nullable = false, precision = 10, scale = 4)
  BigDecimal nbuRateBuy;

  @Column(name = "nbu_rate_sell", nullable = false, precision = 10, scale = 4)
  BigDecimal nbuRateSell;

  @Column(name = "ua_price_buy")
  BigDecimal uaPriceBuy;

  @Column(name = "ua_price_sell")
  BigDecimal uaPriceSell;

  @Column(name = "ua_brutto", nullable = false, precision = 10, scale = 4)
  BigDecimal uaBrutto;

  @Column(name = "tax_18", nullable = false, precision = 10, scale = 4)
  BigDecimal tax18;

  @Column(name = "military_tax_5", nullable = false, precision = 10, scale = 4)
  BigDecimal militaryTax5;

  @Column(name = "tax_sum", nullable = false, precision = 10, scale = 4)
  BigDecimal taxSum;

  @Column(name = "is_military")
  Boolean isMilitary;
}

package com.assets.board.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rates",
        uniqueConstraints = @UniqueConstraint(columnNames = "date"))
@Data
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(name = "nbu_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal nbuRate;

    @Column(nullable = false)
    private String currency = "USD"; // if you track multiple currencies
}

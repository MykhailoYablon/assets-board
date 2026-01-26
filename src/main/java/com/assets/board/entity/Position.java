package com.assets.board.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "mark_price")
    private Double markPrice;

    @Column(name = "position_value")
    private Double positionValue;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "cost_basis_price")
    private Double costBasisPrice;

    @Column(name = "fifo_pnl_unrealized")
    private Double fifoPnlUnrealized;

    @Column(name = "holding_period_date_time")
    private String holdingPeriodDateTime;

    @Column(name = "weight")
    private Double weight;
}

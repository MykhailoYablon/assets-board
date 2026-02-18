package com.assets.board.model.ib;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class IBPosition {

    private Long id;

    @CsvBindByName(column = "Symbol")
    private String symbol;

    @CsvBindByName(column = "Description")
    private String description;

    @CsvBindByName(column = "Quantity")
    private Integer quantity;

    @CsvBindByName(column = "MarkPrice")
    private Double markPrice;

    @CsvBindByName(column = "PositionValue")
    private Double positionValue;

    @CsvBindByName(column = "OpenPrice")
    private Double openPrice;

    @CsvBindByName(column = "CostBasisPrice")
    private Double costBasisPrice;

    @CsvBindByName(column = "FifoPnlUnrealized")
    private Double fifoPnlUnrealized;

    @CsvBindByName(column = "HoldingPeriodDateTime")
    private String holdingPeriodDateTime;

    @CsvBindByName(column = "Weight")
    private Double weight;
}

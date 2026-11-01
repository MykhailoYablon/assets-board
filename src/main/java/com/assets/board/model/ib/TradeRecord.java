package com.assets.board.model.ib;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRecord {

    @CsvBindByName(column = "Symbol")
    private String symbol;

    @CsvBindByName(column = "Description")
    private String description;

    @CsvBindByName(column = "AssetClass")
    private String assetClass;

    @CsvBindByName(column = "CurrencyPrimary")
    private String currency;

    @CsvBindByName(column = "TradeDate")
    private String tradeDate;

    @CsvBindByName(column = "OpenDateTime")
    private String openDateTime;

    @CsvBindByName(column = "Quantity")
    private BigDecimal quantity;

    @CsvBindByName(column = "TradePrice")
    private BigDecimal tradePrice;

    @CsvBindByName(column = "FifoPnlRealized")
    private BigDecimal fifoPnlRealized;

    @CsvBindByName(column = "CostBasis")
    private BigDecimal costBasis;

    @CsvBindByName(column = "Proceeds")
    private BigDecimal proceeds;

    @CsvBindByName(column = "IBCommission")
    private BigDecimal ibCommission;

    @CsvBindByName(column = "Taxes")
    private BigDecimal taxes;

    @CsvBindByName(column = "NetCash")
    private BigDecimal netCash;

    @CsvBindByName(column = "Buy/Sell")
    private String side;

    @CsvBindByName(column = "Open/CloseIndicator")
    private String openCloseIndicator;

    @CsvBindByName(column = "LevelOfDetail")
    private String levelOfDetail;
}

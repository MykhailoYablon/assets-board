package com.assets.board.mapper;

import com.assets.board.model.ib.DividendRecord;
import com.assets.board.model.ib.TradeRecord;
import com.assets.board.service.IBFilesParser.DividendData;
import com.assets.board.service.IBFilesParser.TradeData;
import com.assets.board.service.IBFilesParser.WithholdingTaxData;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface IBParserMapper {

    DateTimeFormatter IB_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Mapping(target = "date", expression = "java(java.time.LocalDate.parse(record.getDateTime().split(\";\")[0], IBParserMapper.IB_DATE))")
    DividendData toDividendData(DividendRecord record);

    @Mapping(target = "date", expression = "java(java.time.LocalDate.parse(record.getDateTime().split(\";\")[0], IBParserMapper.IB_DATE))")
    WithholdingTaxData toWithholdingTaxData(DividendRecord record);

    @Mapping(target = "tradeDate", expression = "java(java.time.LocalDate.parse(record.getTradeDate(), IBParserMapper.IB_DATE))")
    @Mapping(target = "quantity", expression = "java(record.getQuantity().abs())")
    @Mapping(target = "ibCommission", expression = "java(record.getIbCommission() != null ? record.getIbCommission().abs() : java.math.BigDecimal.ZERO)")
    @Mapping(target = "openDate", ignore = true)
    @Mapping(target = "proceeds", ignore = true)
    @Mapping(target = "pnl", ignore = true)
    TradeData toTradeData(TradeRecord record);

    @AfterMapping
    default void setTradeComputedFields(@MappingTarget TradeData trade, TradeRecord record) {
        if (record.getOpenDateTime() != null && !record.getOpenDateTime().isBlank()) {
            trade.setOpenDate(LocalDate.parse(record.getOpenDateTime().split(";")[0], IB_DATE));
        }
        if (record.getProceeds() != null && record.getProceeds().compareTo(BigDecimal.ZERO) != 0) {
            trade.setProceeds(record.getProceeds().abs());
        } else {
            trade.setProceeds(record.getFifoPnlRealized().multiply(record.getQuantity().abs()));
        }
    }
}
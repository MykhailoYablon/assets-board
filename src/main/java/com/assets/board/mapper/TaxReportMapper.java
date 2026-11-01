package com.assets.board.mapper;

import com.assets.board.dto.DividendTaxReportDto;
import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.dto.TradeTaxReportDto;
import com.assets.board.entity.DividendTaxReport;
import com.assets.board.entity.TotalTaxReport;
import com.assets.board.entity.TradeTaxReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxReportMapper {

    @Mapping(source = "dividendTaxReports", target = "dividendTaxReportDtos")
    @Mapping(source = "tradeTaxReports", target = "tradeTaxReportDtos")
    TotalTaxReportDto toDto(TotalTaxReport report);

    DividendTaxReportDto toDividendDto(DividendTaxReport report);

    TradeTaxReportDto toTradeDto(TradeTaxReport report);
}

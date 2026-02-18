package com.assets.board.mapper

import com.assets.board.dto.DividendTaxReportDto
import com.assets.board.dto.TotalTaxReportDto
import com.assets.board.entity.DividendTaxReport
import com.assets.board.entity.TotalTaxReport
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TaxReportMapper {
    @Mapping(source = "taxReports", target = "taxReportDtos")
    fun toDto(report: TotalTaxReport?): TotalTaxReportDto?

    fun toDto(report: DividendTaxReport?): DividendTaxReportDto?
}

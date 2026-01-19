package com.assets.board.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaxReportDto {

    TotalDividendTaxDto totalDividendTaxDto;
    List<DividendTaxDto> dividendTaxDtos;
}

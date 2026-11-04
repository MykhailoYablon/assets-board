package com.assets.board.service;

import com.assets.board.dto.TaxDeclarationFormDto;
import com.assets.board.dto.TaxOfficeOptionDto;
import com.assets.board.dto.TaxYearSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaxService {

    TotalTaxReportDto calculateTax(short year, MultipartFile ibReportFile, boolean isMilitary);

    byte[] generateUaTaxDeclarationXml(TaxDeclarationFormDto form) throws Exception;

    TaxYearSummaryDto getTaxYearSummary(short year);

    List<TaxOfficeOptionDto> getTaxOffices();
}

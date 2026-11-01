package com.assets.board.service;

import com.assets.board.dto.DividendTaxSummaryDto;
import com.assets.board.dto.TaxYearSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import org.springframework.web.multipart.MultipartFile;

public interface TaxService {

    TotalTaxReportDto calculateTax(short year, MultipartFile ibReportFile, boolean isMilitary);

    void generateUaTaxDeclarationXml(short year) throws Exception;

    DividendTaxSummaryDto getDividendSummary(short year);

    TaxYearSummaryDto getTaxYearSummary(short year);
}

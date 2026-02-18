package com.assets.board.service;

import com.assets.board.dto.TotalTaxReportDto;
import org.springframework.web.multipart.MultipartFile;

public interface TaxService {

    TotalTaxReportDto calculateDividendTax(short year, MultipartFile ibReportFile, boolean isMilitary);

    void generateUaTaxDeclarationXml(short year) throws Exception;
}

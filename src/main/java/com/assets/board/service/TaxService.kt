package com.assets.board.service

import com.assets.board.dto.TotalTaxReportDto
import org.springframework.web.multipart.MultipartFile

interface TaxService {
    fun calculateDividendTax(year: Short, ibReportFile: MultipartFile, isMilitary: Boolean): TotalTaxReportDto

    @Throws(Exception::class)
    fun generateUaTaxDeclarationXml(year: Short)
}

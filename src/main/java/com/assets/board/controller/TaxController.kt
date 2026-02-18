package com.assets.board.controller

import com.assets.board.dto.TotalTaxReportDto
import com.assets.board.service.TaxService
import com.assets.board.service.impl.XmlGeneratorService
import lombok.AllArgsConstructor
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/taxes")
@AllArgsConstructor
class TaxController(
    val taxService: TaxService,
    val xmlGeneratorService: XmlGeneratorService
) {
    @PostMapping("/dividends")
    fun calculateDividendTaxes(
        @RequestParam("year") year: Short,
        @RequestParam("file") ibReportFile: MultipartFile,
        @RequestParam isMilitary: Boolean
    ): TotalTaxReportDto {
        return taxService.calculateDividendTax(year, ibReportFile, isMilitary)
    }


    @GetMapping("/generate")
    @Throws(Exception::class)
    fun generateXml(@RequestParam("year") year: Short): ResponseEntity<String?> {
        //        taxService.generateUaTaxDeclarationXml();

//        String xml = xmlGeneratorService.generateDeclarXml(declar);

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_XML

        return ResponseEntity.ok()
            .headers(headers)
            .body<String?>("OK")
    }
}
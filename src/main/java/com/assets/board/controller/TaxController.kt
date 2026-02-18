package com.assets.board.controller;

import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.service.TaxService;
import com.assets.board.service.XmlGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/taxes")
@AllArgsConstructor
public class TaxController {


    private final TaxService taxService;
    private final XmlGeneratorService xmlGeneratorService;

    @PostMapping("/dividends")
    public TotalTaxReportDto calculateDividendTaxes(@RequestParam("year") short year,
                                                    @RequestParam("file") MultipartFile ibReportFile,
                                                    @RequestParam boolean isMilitary) {
        return taxService.calculateDividendTax(year, ibReportFile, isMilitary);
    }


    @GetMapping("/generate")
    public ResponseEntity<String> generateXml(@RequestParam("year") short year) throws Exception {

//        taxService.generateUaTaxDeclarationXml();

//        String xml = xmlGeneratorService.generateDeclarXml(declar);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        return ResponseEntity.ok()
                .headers(headers)
                .body("OK");
    }
}
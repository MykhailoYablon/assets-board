package com.assets.board.controller;

import com.assets.board.dto.TaxYearSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.service.TaxService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/taxes")
@AllArgsConstructor
public class TaxController {

    private final TaxService taxService;

    @PostMapping("/dividends")
    public TotalTaxReportDto calculateDividendTaxes(@RequestParam("year") short year,
                                                    @RequestParam("file") MultipartFile ibReportFile,
                                                    @RequestParam boolean isMilitary) {
        return taxService.calculateTax(year, ibReportFile, isMilitary);
    }

    @GetMapping("/summary/{year}")
    public TaxYearSummaryDto getTaxYearSummary(@PathVariable short year) {
        return taxService.getTaxYearSummary(year);
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generateXml(@RequestParam("year") short year) throws Exception {
        taxService.generateUaTaxDeclarationXml(year);
        return ResponseEntity.ok().build();
    }
}

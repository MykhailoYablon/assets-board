package com.assets.board.controller;

import com.assets.board.dto.TaxDeclarationFormDto;
import com.assets.board.dto.TaxOfficeOptionDto;
import com.assets.board.dto.TaxYearSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.service.TaxService;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/offices")
    public List<TaxOfficeOptionDto> getTaxOffices() {
        return taxService.getTaxOffices();
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateXml(@RequestBody TaxDeclarationFormDto form) throws Exception {
        byte[] zip = taxService.generateUaTaxDeclarationXml(form);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename("tax_declaration_" + form.getYear() + ".zip")
            .build());
        return ResponseEntity.ok().headers(headers).body(zip);
    }
}

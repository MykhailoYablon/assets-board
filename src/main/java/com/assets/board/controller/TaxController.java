package com.assets.board.controller;

import com.assets.board.dto.DividendTaxDto;
import com.assets.board.dto.TaxReportDto;
import com.assets.board.dto.TotalDividendTaxDto;
import com.assets.board.service.TaxService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/taxes")
@AllArgsConstructor
public class TaxController {

    private TaxService taxService;

    @PostMapping("/dividends")
    public TaxReportDto calculateDividendTaxes(@RequestParam("file") MultipartFile ibReportFile,
                                                       @RequestParam boolean isMilitary) {
        List<DividendTaxDto> reports = taxService.calculateDividendTax(ibReportFile, isMilitary);

        TotalDividendTaxDto totalDividendTaxDto = taxService.calculateTotals(reports);

        return TaxReportDto.builder()
                .totalDividendTaxDto(totalDividendTaxDto)
                .dividendTaxDtos(reports)
                .build();
    }
}

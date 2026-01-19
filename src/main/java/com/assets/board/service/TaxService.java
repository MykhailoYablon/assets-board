package com.assets.board.service;

import com.assets.board.dto.DividendTaxDto;
import com.assets.board.dto.TotalDividendTaxDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TaxService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;

    private final ExchangeRateService exchangeRateService;
    private final CsvService csvService;
    private final IBDividendParser parser;


    public List<DividendTaxDto> calculateDividendTax(MultipartFile ibReportFile, boolean isMilitary) {
        IBDividendParser.ParsedData parsedData = parseIBFile(ibReportFile);

        var dividends = Optional.ofNullable(parsedData)
                .map(IBDividendParser.ParsedData::getDividends)
                .orElse(Collections.emptyList());

        //withholding Tax is not calculated for now
        var withholdingTaxes = Optional.ofNullable(parsedData)
                .map(IBDividendParser.ParsedData::getWithholdingTaxes)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors
                        .groupingBy(IBDividendParser.WithholdingTaxData::getSymbol,
                                Collectors.mapping(e -> e, Collectors.toList())));

        // Get unique dates
        Set<LocalDate> uniqueDates = dividends.stream()
                .map(IBDividendParser.DividendData::getDate)
                .collect(Collectors.toSet());

        // Pre-fetch/cache all rates
        Map<LocalDate, BigDecimal> rateCache = new HashMap<>();
        for (LocalDate date : uniqueDates) {
            rateCache.put(date, exchangeRateService.getRateForDate(date));
        }

        var reports = dividends.stream()
                .map(dividend -> {
                    DividendTaxDto report = new DividendTaxDto();

                    report.setSymbol(dividend.getSymbol());
                    report.setDate(dividend.getDate());
                    BigDecimal amount = dividend.getAmount();
                    report.setAmount(amount);

                    BigDecimal exchangeRate = rateCache.get(dividend.getDate());

                    report.setNbuRate(exchangeRate);

                    var uaBrutto = exchangeRate.multiply(amount)
                            .setScale(SCALE, ROUNDING_MODE);

                    report.setUaBrutto(uaBrutto);

                    var tax9 = uaBrutto.multiply(BigDecimal.valueOf(0.09))
                            .setScale(SCALE, ROUNDING_MODE);
                    var militaryTax5 = uaBrutto.multiply(BigDecimal.valueOf(0.05))
                            .setScale(SCALE, ROUNDING_MODE);

                    report.setTax9(tax9);
                    report.setMilitaryTax5(militaryTax5);

                    //if you are in military then don't
                    BigDecimal taxSum;
                    if (isMilitary) {
                        taxSum = tax9;
                        report.setTaxSum(tax9);
                    } else {
                        taxSum = tax9.add(militaryTax5);
                        report.setTaxSum(taxSum);
                    }

                    //need to change this
//                    var usTaxUSD = amount.multiply(BigDecimal.valueOf(0.15))
//                            .setScale(SCALE, ROUNDING_MODE);
//                    report.setUsTaxUSD(usTaxUSD);
//
//                    var usTaxUAH = usTaxUSD.multiply(exchangeRate)
//                            .setScale(SCALE, ROUNDING_MODE);
//                    report.setUsTaxUAH(usTaxUAH);
//
//                    BigDecimal totalTaxUAH = taxSum.add(usTaxUAH);
//                    report.setTotalTaxUAH(totalTaxUAH);
//
//                    report.setUsNetto(uaBrutto.subtract(usTaxUAH));
//                    report.setUaNetto(uaBrutto.subtract(totalTaxUAH));
//
//                    //or - 5%
//                    report.setDividends$Netto(amount.multiply(BigDecimal.valueOf(0.29)).setScale(SCALE, ROUNDING_MODE));

                    return report;
                })
                .toList();

//        csvService.writeCsvReport(reports, taxReportTotals);

        return reports;
    }

    public TotalDividendTaxDto calculateTotals(List<DividendTaxDto> reports) {
        TotalDividendTaxDto totals = new TotalDividendTaxDto();

        // Sum amounts first
        totals.setTotalAmount(sum(reports, DividendTaxDto::getAmount));
        var totalUaBrutto = sum(reports, DividendTaxDto::getUaBrutto);
        totals.setTotalUaBrutto(totalUaBrutto);

        // Calculate tax on totals (not sum of individual taxes)
        var totalTax9 = round(totalUaBrutto.multiply(new BigDecimal("0.09")));
        totals.setTotalTax9(totalTax9);

        var totalMilitaryTax5 = round(totalUaBrutto.multiply(new BigDecimal("0.05")));
        totals.setTotalMilitaryTax5(totalMilitaryTax5);

        totals.setTotalTaxSum(round(totalTax9.add(totalMilitaryTax5)));

        // For these, summing individual values makes sense
//        totals.setTotalUsTaxUSD(sum(reports, DividendTaxReport::getUsTaxUSD));
//        totals.setTotalUsTaxUAH(sum(reports, DividendTaxReport::getUsTaxUAH));
//        totals.setTotalTaxUAH(sum(reports, DividendTaxReport::getTotalTaxUAH));
//        totals.setTotalUsNetto(sum(reports, DividendTaxReport::getUsNetto));
//        totals.setTotalUaNetto(sum(reports, DividendTaxReport::getUaNetto));
//        totals.setTotalDividendsNetto(sum(reports, DividendTaxReport::getDividends$Netto));

        return totals;
    }

    private BigDecimal sum(List<DividendTaxDto> reports,
                           Function<DividendTaxDto, BigDecimal> getter) {
        return reports.stream()
                .map(getter)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private IBDividendParser.ParsedData parseIBFile(MultipartFile ibReportFile) {
        String dividendsFile = "U17113716_2025_2025.csv";
        try {
            try (Reader reader = new InputStreamReader(ibReportFile.getInputStream())) {
                return parser.parseFile(reader);
            }
        } catch (Exception e) {
            log.info("Error");
            return null;
        }
    }
}

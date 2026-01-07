package com.assets.board.service;

import com.assets.board.client.NbuDataClient;
import com.assets.board.model.ib.DividendTaxReport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DividendService {

    private final ExchangeRateService exchangeRateService;
    private final CsvService csvService;
    private final IBDividendParser parser;


    public void calculateDividendTax(boolean isMilitary) {
        IBDividendParser.ParsedData parsedData = parseIBFile();

        var dividends = Optional.ofNullable(parsedData)
                .map(IBDividendParser.ParsedData::getDividends)
                .orElse(Collections.emptyList());
        var withholdingTaxes = Optional.ofNullable(parsedData)
                .map(IBDividendParser.ParsedData::getWithholdingTaxes)
                .orElse(Collections.emptyList());

        // Collect all unique dates
        Set<LocalDate> allDates = new HashSet<>();
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
                    DividendTaxReport report = new DividendTaxReport();

                    report.setSymbol(dividend.getSymbol());
                    report.setDate(dividend.getDate());
                    BigDecimal amount = dividend.getAmount();
                    report.setAmount(amount);

                    BigDecimal exchangeRate = rateCache.get(dividend.getDate());

                    report.setNbuRate(exchangeRate);

                    var uaBrutto = exchangeRate.multiply(amount);

                    report.setUaBrutto(uaBrutto);

                    var tax9 = uaBrutto.multiply(BigDecimal.valueOf(0.09));
                    var militaryTax5 = uaBrutto.multiply(BigDecimal.valueOf(0.05));

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

                    //
                    var usTaxUSD = amount.multiply(BigDecimal.valueOf(0.15));
                    report.setUsTaxUSD(usTaxUSD);

                    var usTaxUAH = usTaxUSD.multiply(exchangeRate);
                    report.setUsTaxUAH(usTaxUAH);

                    BigDecimal totalTaxUAH = taxSum.add(usTaxUAH);
                    report.setTotalTaxUAH(totalTaxUAH);

                    report.setUsNetto(uaBrutto.subtract(usTaxUAH));
                    report.setUaNetto(uaBrutto.subtract(totalTaxUAH));

                    //or - 5%
                    report.setDividends$Netto(amount.multiply(BigDecimal.valueOf(0.29)));

                    return report;
                })
                .toList();

        csvService.writeCsvReport(reports);
    }

    private IBDividendParser.ParsedData parseIBFile() {
        String dividendsFile = "U17113716_2025_2025.csv";
        try {
            ClassPathResource resource = new ClassPathResource(dividendsFile);
            Reader reader = new InputStreamReader(resource.getInputStream());
            return parser.parseFile(reader);
        } catch (Exception e) {
            log.info("Error");
            return null;
        }
    }
}

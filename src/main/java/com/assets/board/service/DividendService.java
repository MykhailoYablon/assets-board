package com.assets.board.service;

import com.assets.board.client.NbuDataClient;
import com.assets.board.model.ib.DividendRecord;
import com.assets.board.model.ib.DividendTaxReport;
import com.assets.board.model.ib.WithholdingTaxRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class DividendService {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^([A-Z]+)(?=\\()");

    private final NbuDataClient nbuDataClient;
    private final IBDividendParser parser;


    public void calculateDividendTax() {
        IBDividendParser.ParsedData parsedData = parseIBFile();

        var dividends = Optional.ofNullable(parsedData)
                .map(IBDividendParser.ParsedData::getDividends)
                .orElse(Collections.emptyList());
        var withholdingTaxes = Optional.ofNullable(parsedData)
                .map(IBDividendParser.ParsedData::getWithholdingTaxes)
                .orElse(Collections.emptyList());


        var reports = dividends.stream()
                .map(dividendRecord -> {
                    DividendTaxReport report = new DividendTaxReport();

                    report.setSymbol(dividendRecord.getSymbol());
                    report.setDate(dividendRecord.getDate());
                    report.setAmount(dividendRecord.getAmount());

                    String date = dividendRecord.getDate().toString().replaceAll("-", "");
                    String exchangeRate = nbuDataClient.exchangeRate(date);

                    report.setNbu(exchangeRate);

                    var uaBrutto = new BigDecimal(exchangeRate).multiply(dividendRecord.getAmount());

                    report.setUaBrutto(uaBrutto);

                    var tax9 = uaBrutto.multiply(BigDecimal.valueOf(0.09));
                    var militaryTax5 = uaBrutto.multiply(BigDecimal.valueOf(0.05));

                    report.setTax9(tax9);
                    report.setMilitaryTax5(militaryTax5);
                    report.setTaxSum(tax9.add(militaryTax5));

                    return report;
                })
                .toList();

        log.info("Reports - {}", reports);

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

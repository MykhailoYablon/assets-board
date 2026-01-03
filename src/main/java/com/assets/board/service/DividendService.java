package com.assets.board.service;

import com.assets.board.client.NbuDataClient;
import com.assets.board.model.ib.DividendRecord;
import com.assets.board.model.ib.WithholdingTaxRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
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
        String dividendsFile = "U17113716_2025_2025.csv";
        try {
            ClassPathResource resource = new ClassPathResource(dividendsFile);
            Reader reader = new InputStreamReader(resource.getInputStream());
            IBDividendParser.ParsedData data = parser.parseFile(reader);

            log.info("Date - {}", data);
        } catch (Exception e) {
            log.info("Error");
        }


//        List<WithholdingTaxRecord> withholdingTaxRecords = parseDividendTax();
//        List<DividendRecord> dividendRecords = parseDividendFile();
//
//        dividendRecords.removeLast();

//        List<DividendRecord> list = dividendRecords.stream()
//                .map(dividendRecord -> {
//                    Matcher matcher = SYMBOL_PATTERN.matcher(dividendRecord.getDescription());
//                    if (matcher.find()) {
//                        dividendRecord.setSymbol(matcher.group(1));
//                    }
//                    String date = dividendRecord.getDate().replaceAll("-", "");
//                    String exchangeRate = nbuDataClient.exchangeRate(date, dividendRecord.getCurrency());
//
//                    dividendRecord.setNbu(exchangeRate);
//                    var uaBrutto = Double.parseDouble(exchangeRate) * Double.parseDouble(dividendRecord.getAmount());
//                    dividendRecord.setUaBrutto(uaBrutto);
//
//                    var tax9 = uaBrutto * 0.09;
//                    var militaryTax5 = uaBrutto * 0.05;
//
//                    dividendRecord.setTax9(tax9);
//                    dividendRecord.setMilitaryTax5(militaryTax5);
//                    dividendRecord.setTaxSum(tax9 + militaryTax5);
//
//                    return dividendRecord;
//                })
//                .toList();

        log.info("Test");

    }

    private List<DividendRecord> parseDividendFile() {
        String dividendsFile = "dividends_2025.csv";
        try {
            ClassPathResource resource = new ClassPathResource(dividendsFile);
            Reader reader = new InputStreamReader(resource.getInputStream());

            CsvToBean<DividendRecord> csvToBean = new CsvToBeanBuilder<DividendRecord>(reader)
                    .withType(DividendRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<DividendRecord> data = csvToBean.parse();

            reader.close();

            return data;

        } catch (Exception e) {
            log.info("No such file {}", dividendsFile);
            return Collections.emptyList();
        }
    }

    private List<WithholdingTaxRecord> parseDividendTax() {
        String dividendsFile = "U17113716_2025_2025.csv";
        try {
            ClassPathResource resource = new ClassPathResource(dividendsFile);
            Reader reader = new InputStreamReader(resource.getInputStream());

            CsvToBean<WithholdingTaxRecord> csvToBean = new CsvToBeanBuilder<WithholdingTaxRecord>(reader)
                    .withType(WithholdingTaxRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<WithholdingTaxRecord> data = csvToBean.parse();

            reader.close();

            return data;

        } catch (Exception e) {
            log.info("No such file {}", dividendsFile);
            return Collections.emptyList();
        }
    }

}

package com.assets.board.service;

import com.assets.board.model.DividendRecord;
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

    private final String NBU_EXCHANGE =
            "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?" +
                    "json&valcode=USD&date=20250331";

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^([A-Z]+)(?=\\()");


    public void calculateDividendTax() {
        List<DividendRecord> dividendRecords = parseFile();

        List<DividendRecord> list = dividendRecords.stream()
                .map(dividendRecord -> {
                    Matcher matcher = SYMBOL_PATTERN.matcher(dividendRecord.getDescription());
                    if (matcher.find()) {
                        dividendRecord.setSymbol(matcher.group(1));
                    }
                    return dividendRecord;
                })
                .toList();

        list.forEach(e -> log.info(String.valueOf(e)));

    }

    private List<DividendRecord> parseFile() {
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

}

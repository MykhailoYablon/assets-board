package com.assets.board.service;

import com.assets.board.model.ib.DividendTaxReport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CsvService {

    private static final String DIVIDEND_REPORT_CSV_HEADER = "Stock Symbol,Дата отримання,Дивіденди в $ Brutto," +
            "Дивіденди в UAH Brutto, НБУ курс в цей день,Податок 9% UAH,Військовий збір 5%,Сума 9% + 5%," +
            "Стягнено в США 15%,Стягнено в США 15% UAH," +
            "Загально податку UAH,Netto US -15%,Netto UAH,Дивіденди Netto $\n";

    private static final String NBU_EXCHANGE_RATE = "Дата,Курс $";

    public void writeCsvReport(List<DividendTaxReport> taxReportList) {
        new File("exports/").mkdirs();
        String filePath = "exports/dividendsTaxReport.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.print(DIVIDEND_REPORT_CSV_HEADER);

            // Write data rows
            taxReportList
                    .forEach(data -> writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            data.getSymbol(),
                            data.getDate(),
                            data.getAmount(),
                            data.getUaBrutto(),
                            data.getNbuRate(),
                            data.getTax9(),
                            data.getMilitaryTax5(),
                            data.getTaxSum(),
                            data.getUsTaxUSD(),
                            data.getUsTaxUAH(),
                            data.getTotalTaxUAH(),
                            data.getUsNetto(),
                            data.getUaNetto(),
                            data.getDividends$Netto()
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNbuToCsv(String date, BigDecimal exchangeRate) {
        new File("exports/").mkdirs();
        String filePath = "exports/exchangeRateNBU.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.print(NBU_EXCHANGE_RATE);

            writer.printf("%s,%s%n",
                    date, exchangeRate);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

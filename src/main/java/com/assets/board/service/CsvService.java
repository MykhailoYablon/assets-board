package com.assets.board.service;

import com.assets.board.dto.DividendTaxReportDto;
import com.assets.board.dto.TotalTaxReportDto;
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
            "Дивіденди в UAH Brutto,НБУ курс в цей день,Податок 9% UAH,Військовий збір 5%,Сума 9% + 5%," +
            "Стягнено в США 15%,Стягнено в США 15% UAH," +
            "Загально податку UAH,Netto US -15%,Netto UAH,Дивіденди Netto $\n";

    private static final String NBU_EXCHANGE_RATE = "Дата,Курс $";

    public void writeCsvReport(List<DividendTaxReportDto> taxReportList, TotalTaxReportDto totals) {
        new File("exports/").mkdirs();
        String filePath = "exports/dividendsTaxReport.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.print(DIVIDEND_REPORT_CSV_HEADER);

            // Write data rows
            taxReportList
                    .forEach(data -> writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            data.getSymbol(),
                            data.getDate(),
                            data.getAmount(), //Дивіденди в $ Brutto
                            data.getUaBrutto(), //Дивіденди в UAH Brutto
                            data.getNbuRate(), //НБУ курс в цей день
                            data.getTax9(), //Податок 9% UAH
                            data.getMilitaryTax5(), //Військовий збір 5%
                            data.getTaxSum() //Сума 9% + 5%
                            //Перерахувати
//                            data.getUsTaxUSD(), //Стягнено в США 15%
//                            data.getUsTaxUAH(), //Стягнено в США 15% UAH
//                            data.getTotalTaxUAH(), //Загально податку UAH
//                            data.getUsNetto(), //Netto US -15%
//                            data.getUaNetto(), //Netto UAH
//                            data.getDividends$Netto() //Дивіденди Netto $
                    ));
            // Write totals row (empty strings for non-summable columns)
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    "TOTAL",           // Symbol column
                    "",                // Date column (empty)
                    totals.getTotalAmount(),
                    totals.getTotalUaBrutto(),
                    "",                // NBU Rate column (empty - no average makes sense)
                    totals.getTotalTax9(),
                    totals.getTotalMilitaryTax5(),
                    totals.getTotalTaxSum()
//                    totals.getTotalUsTaxUSD(),
//                    totals.getTotalUsTaxUAH(),
//                    totals.getTotalTaxUAH(),
//                    totals.getTotalUsNetto(),
//                    totals.getTotalUaNetto(),
//                    totals.getTotalDividendsNetto()
            );
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

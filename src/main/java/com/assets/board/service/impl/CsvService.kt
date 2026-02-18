package com.assets.board.service.impl

import com.assets.board.dto.DividendTaxReportDto
import com.assets.board.dto.TotalTaxReportDto
import lombok.AllArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.math.BigDecimal
import java.util.function.Consumer

@Slf4j
@Service
@AllArgsConstructor
class CsvService {
    fun writeCsvReport(taxReportList: MutableList<DividendTaxReportDto>, totals: TotalTaxReportDto) {
        File("exports/").mkdirs()
        val filePath = "exports/dividendsTaxReport.csv"

        try {
            PrintWriter(FileWriter(filePath)).use { writer ->
                writer.print(DIVIDEND_REPORT_CSV_HEADER)

                taxReportList.forEach { data ->
                    writer.println("${data.symbol},${data.date},${data.amount},${data.uaBrutto},${data.nbuRate},${data.tax9},${data.militaryTax5},${data.taxSum}")
                }

                writer.println("TOTAL,,${totals.totalAmount},${totals.totalUaBrutto},,${totals.totalTax9},${totals.totalMilitaryTax5},${totals.totalTaxSum}")
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun writeNbuToCsv(date: String?, exchangeRate: BigDecimal?) {
        File("exports/").mkdirs()
        val filePath = "exports/exchangeRateNBU.csv"

        try {
            PrintWriter(FileWriter(filePath)).use { writer ->
                // Write header
                writer.print(NBU_EXCHANGE_RATE)
                writer.printf(
                    "%s,%s%n",
                    date, exchangeRate
                )
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val DIVIDEND_REPORT_CSV_HEADER = "Stock Symbol,Дата отримання,Дивіденди в $ Brutto," +
                "Дивіденди в UAH Brutto,НБУ курс в цей день,Податок 9% UAH,Військовий збір 5%,Сума 9% + 5%," +
                "Стягнено в США 15%,Стягнено в США 15% UAH," +
                "Загально податку UAH,Netto US -15%,Netto UAH,Дивіденди Netto $\n"

        private const val NBU_EXCHANGE_RATE = "Дата,Курс $"
    }
}

package com.assets.board.service.parser

import com.assets.board.model.ib.DividendRecord
import com.assets.board.model.ib.IBPosition
import com.assets.board.model.ib.WithholdingTaxRecord
import com.opencsv.bean.CsvToBeanBuilder
import lombok.Data
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.math.BigDecimal
import java.time.LocalDate
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

@Slf4j
@Service
class IBFilesParser {
    private val logger = KotlinLogging.logger {}
    @Data
    class DividendData {
        var symbol: String? = null
        var date: LocalDate? = null
        var amount: BigDecimal? = null
    }

    @Data
    class WithholdingTaxData {
        var symbol: String? = null
        var date: LocalDate? = null
        var amount: BigDecimal? = null
    }

    @Data
    class ParsedData {
        var dividends: MutableList<DividendData?> = ArrayList()
        var withholdingTaxes: MutableList<WithholdingTaxData?> = ArrayList()
    }

    fun parseIBPositions(ibReportFile: MultipartFile): MutableList<IBPosition?>? {
        try {
            InputStreamReader(ibReportFile.inputStream).use { reader ->
                val csvToBean = CsvToBeanBuilder<IBPosition?>(reader)
                    .withType(IBPosition::class.java)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                return csvToBean.parse()
            }
        } catch (e: Exception) {
            logger.info("Error {e}", e)
            return mutableListOf()
        }
    }

    @Throws(Exception::class)
    fun parseDividendFile(reader: Reader): ParsedData {
        val bufferedReader = BufferedReader(reader)

        val dividendLines: MutableList<String?> = ArrayList()
        val withholdingTaxLines: MutableList<String?> = ArrayList()

        var line: String?
        var currentSection: String? = null

        while ((bufferedReader.readLine().also { line = it }) != null) {
            val parts: Array<String?> = line!!.split(",".toRegex(), limit = 2).toTypedArray()

            if (parts.isNotEmpty()) {
                val firstColumn = parts[0]!!.trim { it <= ' ' }

                // Check if this is a header line
                if (line.contains(",Header,")) {
                    currentSection = firstColumn
                } else if (line.contains(",Data,")) {
                    if ("Dividends" == currentSection) {
                        dividendLines.add(line)
                    } else if ("Withholding Tax" == currentSection) {
                        withholdingTaxLines.add(line)
                    }
                }
            }
        }

        val result = ParsedData()
        result.dividends = parseDividends(dividendLines)
        result.withholdingTaxes = parseWithholdingTaxes(withholdingTaxLines)

        return result
    }

    private fun parseDividends(lines: MutableList<String?>): MutableList<DividendData?> {
        if (lines.isEmpty()) return mutableListOf()

        val csv = lines.joinToString("\n")

        return try {
            StringReader(csv).use { stringReader ->
                CsvToBeanBuilder<DividendRecord>(stringReader)
                    .withType(DividendRecord::class.java)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse()
                    .filter { r -> !r.date.isNullOrBlank() }
                    .map { r ->
                        DividendData().apply {
                            symbol = extractSymbol(r.description)
                            date = LocalDate.parse(r.date)
                            amount = r.amount
                        }
                    }
                    .toMutableList()
            }
        } catch (e: Exception) {
            throw RuntimeException("Error parsing dividends", e)
        }
    }

    private fun parseWithholdingTaxes(lines: MutableList<String?>): MutableList<WithholdingTaxData?> {
        if (lines.isEmpty()) return mutableListOf()

        val csv = lines.joinToString("\n")
        return try {
            StringReader(csv).use { stringReader ->
                CsvToBeanBuilder<WithholdingTaxRecord>(stringReader)
                    .withType(WithholdingTaxRecord::class.java)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse()
                    .filter { r -> !r.date.isNullOrBlank() }
                    .map { r ->
                        WithholdingTaxData().apply {
                            symbol = extractSymbol(r.description)
                            date = LocalDate.parse(r.date)
                            amount = r.amount
                        }
                    }
                    .toMutableList()
            }
        } catch (e: Exception) {
            throw RuntimeException("Error parsing withholding taxes", e)
        }
    }

    private fun extractSymbol(description: String?): String? {
        if (description == null) return null
        val matcher: Matcher = SYMBOL_PATTERN.matcher(description)
        return if (matcher.find()) matcher.group(1) else null
    }

    companion object {
        private val SYMBOL_PATTERN: Pattern = Pattern.compile("^([A-Z]+)(?=\\()")
    }
}
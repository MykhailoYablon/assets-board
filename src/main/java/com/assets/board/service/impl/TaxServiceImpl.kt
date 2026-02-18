package com.assets.board.service.impl

import com.assets.board.dto.TotalTaxReportDto
import com.assets.board.entity.DividendTaxReport
import com.assets.board.entity.TotalTaxReport
import com.assets.board.mapper.TaxReportMapper
import com.assets.board.model.enums.ReportStatus
import com.assets.board.model.tax.*
import com.assets.board.repository.TotalTaxReportRepository
import com.assets.board.service.TaxService
import com.assets.board.service.parser.IBFilesParser
import com.assets.board.service.parser.IBFilesParser.DividendData
import com.assets.board.service.parser.IBFilesParser.ParsedData
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

@Service
class TaxServiceImpl(
    val exchangeRateService: ExchangeRateService,
    val xmlGeneratorService: XmlGeneratorService,
    val parser: IBFilesParser,
    val totalTaxReportRepository: TotalTaxReportRepository,
    val taxReportMapper: TaxReportMapper
) : TaxService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun calculateDividendTax(
        year: Short,
        ibReportFile: MultipartFile,
        isMilitary: Boolean
    ): TotalTaxReportDto {
        val parsedData = parseIBFile(ibReportFile)
        val dividends = parsedData?.dividends ?: emptyList()

        val rateCache = buildRateCache(dividends)

        val reports = dividends.map({ dividend ->
            dividend?.let { calculateDividendReport(it, rateCache, isMilitary) }
        }
        ).toMutableList()

        val totalReport = findOrCreateTotalReport(year).apply {
            calculateTotals(this, reports)
            this.taxReports.clear()
            reports.forEach(Consumer { report: DividendTaxReport? -> this.addTaxReport(report!!) })
            status = ReportStatus.CALCULATED
        }

        return taxReportMapper.toDto(totalTaxReportRepository.save(totalReport))!!
    }

    private fun buildRateCache(dividends: List<DividendData?>): Map<LocalDate?, BigDecimal?> {
        return dividends
            .map { it?.date }
            .toSet()
            .associateWith { date -> exchangeRateService.getRateForDate(date) }
    }

    private fun calculateDividendReport(
        dividend: DividendData,
        rateCache: Map<LocalDate?, BigDecimal?>,
        isMilitary: Boolean
    ): DividendTaxReport {
        val exchangeRate = rateCache[dividend.date] ?: BigDecimal.ZERO
        val uaBrutto = dividend.amount?.let { (exchangeRate * it).setScale(SCALE, ROUNDING_MODE) }
        val tax9 = (uaBrutto?.times(BigDecimal.valueOf(0.09)))?.setScale(SCALE, ROUNDING_MODE)
        val militaryTax5 = (uaBrutto?.times(BigDecimal.valueOf(0.05)))?.setScale(SCALE, ROUNDING_MODE)
        val taxSum = if (isMilitary) tax9 else militaryTax5?.let { tax9?.plus(it) }

        return DividendTaxReport().apply {
            symbol = dividend.symbol
            date = dividend.date
            amount = dividend.amount
            nbuRate = exchangeRate
            this.uaBrutto = uaBrutto
            this.tax9 = tax9
            this.militaryTax5 = militaryTax5
            this.taxSum = taxSum
        }
    }

    private fun findOrCreateTotalReport(year: Short): TotalTaxReport {
        return totalTaxReportRepository.findByYear(year)
            ?: TotalTaxReport().apply { this.year = year }
    }

    @Transactional
    @Throws(Exception::class)
    override fun generateUaTaxDeclarationXml(year: Short) {
        val existingReport = totalTaxReportRepository.findByYear(year)
            ?: TotalTaxReport().apply { this.year = year }

        val uuid = UUID.randomUUID()
        val mainFilename = String.format("F0100214_Zvit_%s.xml", uuid)
        val f1Filename = String.format("F0121214_DodatokF1_%s.xml", uuid)

        val mainFilePath: String = EXPORT_DIR + File.separator + mainFilename
        val f1FilePath: String = EXPORT_DIR + File.separator + f1Filename

        val mainDeclar = createDeclar(uuid, existingReport)
        val f1Declar = createDeclarF1(uuid, existingReport)

        xmlGeneratorService.saveXmlToFile(mainDeclar, mainFilePath)
        xmlGeneratorService.saveXmlToFile(f1Declar, f1FilePath)
    }

    private fun calculateTotals(
        taxReport: TotalTaxReport,
        reports: MutableList<DividendTaxReport?>
    ): TotalTaxReport {
        // Sum amounts first
        taxReport.apply {
            totalAmount = sum(reports, DividendTaxReport::amount)
            totalUaBrutto = sum(reports, DividendTaxReport::uaBrutto)
            totalTax9 = totalUaBrutto?.let { doRound(it * BigDecimal("0.09")) }
            totalMilitaryTax5 = totalUaBrutto?.let { doRound(it * BigDecimal("0.05")) }
            totalTaxSum = totalTax9?.let { totalMilitaryTax5?.let { it1 -> doRound(it + it1) } }
        }
        return taxReport
    }

    private fun parseIBFile(ibReportFile: MultipartFile): ParsedData? {
        try {
            InputStreamReader(ibReportFile.inputStream).use { reader ->
                return parser.parseDividendFile(reader)
            }
        } catch (e: Exception) {
            logger.info("Error {e}", e)
            return null
        }
    }

    private fun sum(
        reports: MutableList<DividendTaxReport>,
        getter: Function<DividendTaxReport, BigDecimal>
    ): BigDecimal {
        return reports.stream()
            .map<BigDecimal>(getter)
            .reduce(BigDecimal.ZERO) { obj: BigDecimal?, augend: BigDecimal? -> obj!!.add(augend) }
    }

    private fun createDeclar(uuid: UUID?, existingReport: TotalTaxReport?): Declar {
        return Declar().apply {
            declarHead = DeclarHead().apply {
                tin = ""
                cDoc = "F01"
                cDocSub = "002"
                cDocVer = "11"
                cDocType = "0"
                cDocCnt = "1"
                cReg = "0"
                cRaj = "15"
                periodMonth = "12"
                periodType = "5"
                periodYear = "2025"
                cStiOrig = "915"
                cDocStan = "1"
                dFill = "16012026"

                linkedDocs = LinkedDocs().apply {
                    docs = mutableListOf(
                        Doc().apply {
                            num = "1"
                            type = "1"
                            cDoc = "F01"
                            cDocSub = "212"
                            cDocVer = "11"
                            cDocType = "1"
                            cDocCnt = "1"
                            cDocStan = "1"
                            filename = "F0121214_DodatokF1_$uuid.xml"
                        }
                    )
                }
            }

            declarBody = DeclarBody().apply {
                h01 = "1"
                h03 = "1"
                h05 = "1"
                hbos = "Test"
                hcity = "Test"
                hd1 = "1"
                hfill = "16012026"
                hname = "Test Test"
                hsti = "ДПС"
                hstreet = "Stree"
                htin = ""
                hz = "1"
                hzy = "2025"
                r0104g3 = BigDecimal.valueOf(2323.86)
                r0104g6 = BigDecimal.valueOf(209.15)
                r0104g7 = BigDecimal.ZERO
                r0108g3 = BigDecimal.ZERO
                r0108g6 = BigDecimal.ZERO
                r0108g7 = BigDecimal.ZERO
                r01010g2s = "Інші проценти"
                r010g3 = BigDecimal.ZERO
                r010g6 = BigDecimal.ZERO
                r010g7 = BigDecimal.ZERO
                r012g3 = BigDecimal.ZERO
                r013g3 = BigDecimal.ZERO
                r018g3 = BigDecimal.ZERO
                r0201g3 = BigDecimal.ZERO
                r0211g3 = BigDecimal.ZERO
            }
        }
    }

    private fun createDeclarF1(uuid: UUID?, existingReport: TotalTaxReport?): Declar {
        return Declar().apply {
            declarHead = DeclarHead().apply {
                tin = ""
                cDoc = "F01"
                cDocSub = "212"
                cDocVer = "11"
                cDocType = "1"
                cDocCnt = "1"
                cReg = "0"
                cRaj = "15"
                periodMonth = "12"
                periodType = "5"
                periodYear = "2025"
                cStiOrig = "915"
                cDocStan = "1"
                dFill = "16012026"

                linkedDocs = LinkedDocs().apply {
                    docs = mutableListOf(
                        Doc().apply {
                            num = "1"
                            type = "2"
                            cDoc = "F01"
                            cDocSub = "002"
                            cDocVer = "11"
                            cDocType = "0"
                            cDocCnt = "1"
                            cDocStan = "1"
                            filename = "F0100214_Zvit_$uuid.xml"
                        }
                    )
                }
            }

            declarBody = DeclarBodyF1().apply {
                hbos = "Test Test"
                htin = ""
                hz = "1"
                hzy = "2025"
                r001g4 = "32130.56"
                r001g5 = "31196.75"
                r001g6 = "933.81"
                r003g6 = "933.81"
                r004g6 = "168.09"
                r005g6 = "46.69"
                r031g6 = "933.81"
                r042g6 = "168.09"
                r052g6 = "46.69"

                // Add table rows
                addTableRow("1", "4", "GOOGL 1шт.", "6903.66", "6259.61", "644.05")
                addTableRow("2", "4", "CRM 1шт.", "11047.23", "11016.3", "30.93")
                addTableRow("3", "4", "VTI 1шт.", "14179.67", "13920.84", "258.83")
            }
        }
    }

    fun doRound(value: BigDecimal): BigDecimal {
        return value.setScale(2, RoundingMode.HALF_UP)
    }

    private fun sum(reports: MutableList<DividendTaxReport?>, getter: (DividendTaxReport) -> BigDecimal?): BigDecimal =
        reports.fold(BigDecimal.ZERO) { acc, report -> acc + (report?.let { getter(it) } ?: BigDecimal.ZERO) }

    companion object {
        private const val SCALE = 2
        private val ROUNDING_MODE = RoundingMode.HALF_DOWN
        private const val EXPORT_DIR = "exports"


    }
}

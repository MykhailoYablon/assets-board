package com.assets.board.service;

import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.entity.DividendTaxReport;
import com.assets.board.entity.TotalTaxReport;
import com.assets.board.mapper.TaxReportMapper;
import com.assets.board.model.tax.*;
import com.assets.board.repository.TotalTaxReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private static final String EXPORT_DIR = "exports";

    private final ExchangeRateService exchangeRateService;
    private final CsvService csvService;
    private final XmlGeneratorService xmlGeneratorService;
    private final IBFilesParser parser;
    private final TotalTaxReportRepository totalTaxReportRepository;
    private final TaxReportMapper taxReportMapper;


    public TotalTaxReportDto calculateDividendTax(short year, MultipartFile ibReportFile, boolean isMilitary) {
        IBFilesParser.ParsedData parsedData = parseIBFile(ibReportFile);

        var dividends = Optional.ofNullable(parsedData)
                .map(IBFilesParser.ParsedData::getDividends)
                .orElse(Collections.emptyList());

        //withholding Tax is not calculated for now
        var withholdingTaxes = Optional.ofNullable(parsedData)
                .map(IBFilesParser.ParsedData::getWithholdingTaxes)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors
                        .groupingBy(IBFilesParser.WithholdingTaxData::getSymbol,
                                Collectors.mapping(e -> e, Collectors.toList())));

        // Get unique dates
        Set<LocalDate> uniqueDates = dividends.stream()
                .map(IBFilesParser.DividendData::getDate)
                .collect(Collectors.toSet());

        // Pre-fetch/cache all rates
        Map<LocalDate, BigDecimal> rateCache = new HashMap<>();
        for (LocalDate date : uniqueDates) {
            rateCache.put(date, exchangeRateService.getRateForDate(date));
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

//        csvService.writeCsvReport(reports, taxReportTotals);

        var reports = dividends.stream()
                .map(dividend -> {
                    DividendTaxReport report = new DividendTaxReport();

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

        Optional<TotalTaxReport> existingReport = totalTaxReportRepository.findByYear(year);

        TotalTaxReport totalReport;

        if (existingReport.isPresent()) {
            // Update existing report
            totalReport = existingReport.get();
        } else {
            // Create new report
            totalReport = new TotalTaxReport();
            totalReport.setYear(year);
        }


        this.calculateTotals(totalReport, reports);
        totalReport.setTaxReports(reports);
        totalReport.setYear(year);

        TotalTaxReport report = totalTaxReportRepository.save(totalReport);


        return taxReportMapper.toDto(report);
    }

    public void generateUaTaxDeclarationXml(TotalTaxReportDto taxReportDto) throws Exception {
        UUID uuid = UUID.randomUUID();
        String mainFilename = String.format("F0100214_Zvit_%s.xml", uuid);
        String f1Filename = String.format("F0121214_DodatokF1_%s.xml", uuid);

        String mainFilePath = EXPORT_DIR + File.separator + mainFilename;
        String f1FilePath = EXPORT_DIR + File.separator + f1Filename;

        Declar mainDeclar = createSampleDeclar(uuid);
        Declar f1Declar = createDeclarF1(uuid);

        xmlGeneratorService.saveXmlToFile(mainDeclar, mainFilePath);
        xmlGeneratorService.saveXmlToFile(f1Declar, f1FilePath);
    }

    private TotalTaxReport calculateTotals(TotalTaxReport taxReport, List<DividendTaxReport> reports) {
        // Sum amounts first
        taxReport.setTotalAmount(sum(reports, DividendTaxReport::getAmount));
        var totalUaBrutto = sum(reports, DividendTaxReport::getUaBrutto);
        taxReport.setTotalUaBrutto(totalUaBrutto);

        // Calculate tax on taxReport (not sum of individual taxes)
        var totalTax9 = round(totalUaBrutto.multiply(new BigDecimal("0.09")));
        taxReport.setTotalTax9(totalTax9);

        var totalMilitaryTax5 = round(totalUaBrutto.multiply(new BigDecimal("0.05")));
        taxReport.setTotalMilitaryTax5(totalMilitaryTax5);

        taxReport.setTotalTaxSum(round(totalTax9.add(totalMilitaryTax5)));

        // For these, summing individual values makes sense
//        taxReport.setTotalUsTaxUSD(sum(reports, DividendTaxReport::getUsTaxUSD));
//        taxReport.setTotalUsTaxUAH(sum(reports, DividendTaxReport::getUsTaxUAH));
//        taxReport.setTotalTaxUAH(sum(reports, DividendTaxReport::getTotalTaxUAH));
//        taxReport.setTotalUsNetto(sum(reports, DividendTaxReport::getUsNetto));
//        taxReport.setTotalUaNetto(sum(reports, DividendTaxReport::getUaNetto));
//        taxReport.setTotalDividendsNetto(sum(reports, DividendTaxReport::getDividends$Netto));

        return taxReport;
    }

    private IBFilesParser.ParsedData parseIBFile(MultipartFile ibReportFile) {
        try {
            try (Reader reader = new InputStreamReader(ibReportFile.getInputStream())) {
                return parser.parseDividendFile(reader);
            }
        } catch (Exception e) {
            log.info("Error");
            return null;
        }
    }

    private BigDecimal sum(List<DividendTaxReport> reports,
                           Function<DividendTaxReport, BigDecimal> getter) {
        return reports.stream()
                .map(getter)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private Declar createSampleDeclar(UUID uuid) {
        Declar declar = new Declar();

        DeclarHead head = new DeclarHead();
        head.setTin("");
        head.setCDoc("F01");
        head.setCDocSub("002");
        head.setCDocVer("11");
        head.setCDocType("0");
        head.setCDocCnt("1");
        head.setCReg("0");
        head.setCRaj("15");
        head.setPeriodMonth("12");
        head.setPeriodType("5");
        head.setPeriodYear("2025");
        head.setCStiOrig("915");
        head.setCDocStan("1");

        //date of generation
        head.setDFill("16012026");
//        head.setSoftware("https://ua-tax.web.app/ 1.7.0");

        // Create linked docs
        Doc doc = new Doc();
        doc.setNum("1");
        doc.setType("1");
        doc.setCDoc("F01");
        doc.setCDocSub("212");
        doc.setCDocVer("11");
        doc.setCDocType("1");
        doc.setCDocCnt("1");
        doc.setCDocStan("1");

        doc.setFilename("F0121214_DodatokF1_" + uuid + ".xml");

        LinkedDocs linkedDocs = new LinkedDocs();
        linkedDocs.setDocs(Arrays.asList(doc));

        head.setLinkedDocs(linkedDocs);
        declar.setDeclarHead(head);

        // DECLARBODY
        DeclarBody body = new DeclarBody();
        body.setH01("1");
        body.setH03("1");
        body.setH05("1");
        body.setHbos("Test");
        body.setHcity("Test");
        body.setHd1("1");
        body.setHfill("16012026");
        body.setHname("Test Test");
        body.setHsti("ДПС");
        body.setHstreet("Stree");
        body.setHtin("");
        body.setHz("1");
        body.setHzy("2025");
        body.setR0104g3(BigDecimal.valueOf(2323.86));
        body.setR0104g6(BigDecimal.valueOf(209.15));
        body.setR0104g7(BigDecimal.ZERO);
        body.setR0108g3(BigDecimal.ZERO);
        body.setR0108g6(BigDecimal.ZERO);
        body.setR0108g7(BigDecimal.ZERO);
        body.setR01010g2s("Інші проценти");
        body.setR010g3(BigDecimal.ZERO);
        body.setR010g6(BigDecimal.ZERO);
        body.setR010g7(BigDecimal.ZERO);
        body.setR012g3(BigDecimal.ZERO);
        body.setR013g3(BigDecimal.ZERO);
        body.setR018g3(BigDecimal.ZERO);
        body.setR0201g3(BigDecimal.ZERO);
        body.setR0211g3(BigDecimal.ZERO);

        declar.setDeclarBody(body);

        return declar;
    }

    private Declar createDeclarF1(UUID uuid) {
        Declar declar = new Declar();

        // DECLARHEAD
        DeclarHead head = new DeclarHead();
        head.setTin("");
        head.setCDoc("F01");
        head.setCDocSub("212");
        head.setCDocVer("11");
        head.setCDocType("1");
        head.setCDocCnt("1");
        head.setCReg("0");
        head.setCRaj("15");
        head.setPeriodMonth("12");
        head.setPeriodType("5");
        head.setPeriodYear("2025");
        head.setCStiOrig("915");
        head.setCDocStan("1");
        head.setDFill("16012026");

        // Create linked docs
        Doc doc = new Doc();
        doc.setNum("1");
        doc.setType("2");
        doc.setCDoc("F01");
        doc.setCDocSub("002");
        doc.setCDocVer("11");
        doc.setCDocType("0");
        doc.setCDocCnt("1");
        doc.setCDocStan("1");
        doc.setFilename("F0100214_Zvit_" + uuid + ".xml");

        LinkedDocs linkedDocs = new LinkedDocs();
        linkedDocs.setDocs(Arrays.asList(doc));
        head.setLinkedDocs(linkedDocs);

        declar.setDeclarHead(head);

        // DECLARBODY F1
        DeclarBodyF1 body = new DeclarBodyF1();
        body.setHbos("Test Test");
        body.setHtin("");
        body.setHz("1");
        body.setHzy("2025");
        body.setR001g4("32130.56");
        body.setR001g5("31196.75");
        body.setR001g6("933.81");
        body.setR003g6("933.81");
        body.setR004g6("168.09");
        body.setR005g6("46.69");
        body.setR031g6("933.81");
        body.setR042g6("168.09");
        body.setR052g6("46.69");

        // Add table rows
        body.addTableRow("1", "4", "GOOGL 1шт.", "6903.66", "6259.61", "644.05");
        body.addTableRow("2", "4", "CRM 1шт.", "11047.23", "11016.3", "30.93");
        body.addTableRow("3", "4", "VTI 1шт.", "14179.67", "13920.84", "258.83");

        declar.setDeclarBody(body);

        return declar;
    }
}

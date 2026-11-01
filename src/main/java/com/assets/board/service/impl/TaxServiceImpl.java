package com.assets.board.service.impl;

import com.assets.board.configuration.TaxpayerProperties;
import com.assets.board.controller.exception.ResourceNotFoundException;
import com.assets.board.dto.DividendTaxSummaryDto;
import com.assets.board.dto.SymbolDividendSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.entity.DividendTaxReport;
import com.assets.board.entity.TotalTaxReport;
import com.assets.board.mapper.TaxReportMapper;
import com.assets.board.model.enums.ReportStatus;
import com.assets.board.model.tax.Declar;
import com.assets.board.model.tax.DeclarBody;
import com.assets.board.model.tax.DeclarBodyF1;
import com.assets.board.model.tax.DeclarHead;
import com.assets.board.model.tax.Doc;
import com.assets.board.model.tax.LinkedDocs;
import com.assets.board.repository.TotalTaxReportRepository;
import com.assets.board.service.CsvService;
import com.assets.board.service.ExchangeRateService;
import com.assets.board.service.IBFilesParser;
import com.assets.board.service.TaxService;
import com.assets.board.service.XmlGeneratorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TaxServiceImpl implements TaxService {

  private static final int SCALE = 2;
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;
  private static final String EXPORT_DIR = "exports";
  private static final DateTimeFormatter FILL_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

  private final ExchangeRateService exchangeRateService;
  private final CsvService csvService;
  private final XmlGeneratorService xmlGeneratorService;
  private final IBFilesParser parser;
  private final TotalTaxReportRepository totalTaxReportRepository;
  private final TaxReportMapper taxReportMapper;
  private final TaxpayerProperties taxpayer;


  @Override
  @Transactional
  public TotalTaxReportDto calculateTax(short year, MultipartFile ibReportFile, boolean isMilitary) {
    IBFilesParser.ParsedData parsedData = parser.parseIBFile(ibReportFile);

    var dividends = Optional.ofNullable(parsedData)
        .map(IBFilesParser.ParsedData::getDividends)
        .orElse(Collections.emptyList());

    Set<LocalDate> uniqueDates = dividends.stream()
        .map(IBFilesParser.DividendData::getDate)
        .collect(Collectors.toSet());

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

          var uaBrutto = exchangeRate.multiply(amount).setScale(SCALE, ROUNDING_MODE);
          report.setUaBrutto(uaBrutto);

          var tax9 = uaBrutto.multiply(BigDecimal.valueOf(0.09)).setScale(SCALE, ROUNDING_MODE);
          var militaryTax5 = uaBrutto.multiply(BigDecimal.valueOf(0.05)).setScale(SCALE, ROUNDING_MODE);
          report.setTax9(tax9);
          report.setMilitaryTax5(militaryTax5);

          if (isMilitary) {
            report.setTaxSum(tax9);
          } else {
            report.setTaxSum(tax9.add(militaryTax5));
          }

          return report;
        })
        .toList();

    TotalTaxReport totalReport = totalTaxReportRepository.findByYear(year)
        .orElseGet(TotalTaxReport::new);
    totalReport.setYear(year);

    calculateTotals(totalReport, reports, isMilitary);
    totalReport.setTaxReports(reports);
    totalReport.setStatus(ReportStatus.CALCULATED);

    return taxReportMapper.toDto(totalTaxReportRepository.save(totalReport));
  }

  @Override
  @Transactional
  public void generateUaTaxDeclarationXml(short year) throws Exception {
    TotalTaxReport report = totalTaxReportRepository.findByYear(year)
        .orElseThrow(() ->
            new ResourceNotFoundException(String.format("No reports loaded for year %s", year)));

    new File(EXPORT_DIR).mkdirs();

    UUID uuid = UUID.randomUUID();
    String mainFilePath = EXPORT_DIR + File.separator + String.format("F0100214_Zvit_%s.xml", uuid);
    String f1FilePath = EXPORT_DIR + File.separator + String.format("F0121214_DodatokF1_%s.xml", uuid);

    xmlGeneratorService.saveXmlToFile(createDeclar(uuid, report), mainFilePath);
    xmlGeneratorService.saveXmlToFile(createDeclarF1(uuid, report), f1FilePath);
  }

  @Override
  @Transactional(readOnly = true)
  public DividendTaxSummaryDto getDividendSummary(short year) {
    TotalTaxReport report = totalTaxReportRepository.findByYear(year)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("No reports found for year %s", year)));

    List<SymbolDividendSummaryDto> bySymbol = report.getTaxReports().stream()
        .collect(Collectors.groupingBy(DividendTaxReport::getSymbol))
        .entrySet().stream()
        .map(entry -> {
          List<DividendTaxReport> rows = entry.getValue();
          SymbolDividendSummaryDto dto = new SymbolDividendSummaryDto();
          dto.setSymbol(entry.getKey());
          dto.setTotalAmount(sum(rows, DividendTaxReport::getAmount));
          dto.setTotalUaBrutto(sum(rows, DividendTaxReport::getUaBrutto));
          dto.setTotalTax9(sum(rows, DividendTaxReport::getTax9));
          dto.setTotalMilitaryTax5(sum(rows, DividendTaxReport::getMilitaryTax5));
          dto.setTotalTaxSum(sum(rows, DividendTaxReport::getTaxSum));
          dto.setPayments(rows.stream()
              .sorted(Comparator.comparing(DividendTaxReport::getDate))
              .map(taxReportMapper::toDto)
              .toList());
          return dto;
        })
        .sorted(Comparator.comparing(SymbolDividendSummaryDto::getSymbol))
        .toList();

    DividendTaxSummaryDto summary = new DividendTaxSummaryDto();
    summary.setYear(report.getYear());
    summary.setTotalAmount(report.getTotalAmount());
    summary.setTotalUaBrutto(report.getTotalUaBrutto());
    summary.setTotalTax9(report.getTotalTax9());
    summary.setTotalMilitaryTax5(report.getTotalMilitaryTax5());
    summary.setTotalTaxSum(report.getTotalTaxSum());
    summary.setBySymbol(bySymbol);
    return summary;
  }

  private void calculateTotals(TotalTaxReport taxReport, List<DividendTaxReport> reports, boolean isMilitary) {
    taxReport.setTotalAmount(sum(reports, DividendTaxReport::getAmount));

    var totalUaBrutto = sum(reports, DividendTaxReport::getUaBrutto);
    taxReport.setTotalUaBrutto(totalUaBrutto);

    var totalTax9 = round(totalUaBrutto.multiply(new BigDecimal("0.09")));
    taxReport.setTotalTax9(totalTax9);

    var totalMilitaryTax5 = round(totalUaBrutto.multiply(new BigDecimal("0.05")));
    taxReport.setTotalMilitaryTax5(totalMilitaryTax5);

    if (isMilitary) {
      taxReport.setTotalTaxSum(totalTax9);
    } else {
      taxReport.setTotalTaxSum(round(totalTax9.add(totalMilitaryTax5)));
    }
  }

  private BigDecimal sum(List<DividendTaxReport> reports, Function<DividendTaxReport, BigDecimal> getter) {
    return reports.stream()
        .map(getter)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private static BigDecimal round(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private Declar createDeclar(UUID uuid, TotalTaxReport report) {
    String year = String.valueOf(report.getYear());
    String fillDate = LocalDate.now().format(FILL_DATE_FORMAT);

    DeclarHead head = new DeclarHead();
    head.setTin(taxpayer.getTin());
    head.setCDoc("F01");
    head.setCDocSub("002");
    head.setCDocVer("11");
    head.setCDocType("0");
    head.setCDocCnt("1");
    head.setCReg(taxpayer.getCReg());
    head.setCRaj(taxpayer.getCRaj());
    head.setPeriodMonth("12");
    head.setPeriodType("5");
    head.setPeriodYear(year);
    head.setCStiOrig(taxpayer.getCStiOrig());
    head.setCDocStan("1");
    head.setDFill(fillDate);

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
    linkedDocs.setDocs(List.of(doc));
    head.setLinkedDocs(linkedDocs);

    DeclarBody body = new DeclarBody();
    body.setH01("1");
    body.setH03("1");
    body.setH05("1");
    body.setHbos(taxpayer.getBuilding());
    body.setHcity(taxpayer.getCity());
    body.setHd1("1");
    body.setHfill(fillDate);
    body.setHname(taxpayer.getName());
    body.setHsti(taxpayer.getTaxOffice());
    body.setHstreet(taxpayer.getStreet());
    body.setHtin(taxpayer.getTin());
    body.setHz("1");
    body.setHzy(year);
    body.setR0104g3(report.getTotalUaBrutto());
    body.setR0104g6(report.getTotalTax9());
    body.setR0104g7(report.getTotalMilitaryTax5());
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

    Declar declar = new Declar();
    declar.setDeclarHead(head);
    declar.setDeclarBody(body);
    return declar;
  }

  private Declar createDeclarF1(UUID uuid, TotalTaxReport report) {
    String year = String.valueOf(report.getYear());
    String fillDate = LocalDate.now().format(FILL_DATE_FORMAT);

    DeclarHead head = new DeclarHead();
    head.setTin(taxpayer.getTin());
    head.setCDoc("F01");
    head.setCDocSub("212");
    head.setCDocVer("11");
    head.setCDocType("1");
    head.setCDocCnt("1");
    head.setCReg(taxpayer.getCReg());
    head.setCRaj(taxpayer.getCRaj());
    head.setPeriodMonth("12");
    head.setPeriodType("5");
    head.setPeriodYear(year);
    head.setCStiOrig(taxpayer.getCStiOrig());
    head.setCDocStan("1");
    head.setDFill(fillDate);

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
    linkedDocs.setDocs(List.of(doc));
    head.setLinkedDocs(linkedDocs);

    // F1 appendix is for capital gains (investment income from stock sales).
    // Capital gains calculation is not yet implemented — all fields are zero.
    DeclarBodyF1 body = new DeclarBodyF1();
    body.setHbos(taxpayer.getName());
    body.setHtin(taxpayer.getTin());
    body.setHz("1");
    body.setHzy(year);
    body.setR001g4("0");
    body.setR001g5("0");
    body.setR001g6("0");
    body.setR003g6("0");
    body.setR004g6("0");
    body.setR005g6("0");
    body.setR031g6("0");
    body.setR042g6("0");
    body.setR052g6("0");

    Declar declar = new Declar();
    declar.setDeclarHead(head);
    declar.setDeclarBody(body);
    return declar;
  }
}

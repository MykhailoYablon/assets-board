package com.assets.board.service.impl;

import com.assets.board.controller.exception.ResourceNotFoundException;
import com.assets.board.dto.SymbolDividendSummaryDto;
import com.assets.board.dto.SymbolTradeSummaryDto;
import com.assets.board.dto.TaxDeclarationFormDto;
import com.assets.board.dto.TaxOfficeOptionDto;
import com.assets.board.dto.TaxYearSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.model.UkrainianTaxOffice;
import com.assets.board.entity.DividendTaxReport;
import com.assets.board.entity.TotalTaxReport;
import com.assets.board.entity.TradeTaxReport;
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
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@AllArgsConstructor
public class TaxServiceImpl implements TaxService {

  private static final DateTimeFormatter FILL_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

  private final ExchangeRateService exchangeRateService;
  private final CsvService csvService;
  private final XmlGeneratorService xmlGeneratorService;
  private final IBFilesParser parser;
  private final TotalTaxReportRepository totalTaxReportRepository;
  private final TaxReportMapper taxReportMapper;


  @Override
  @Transactional
  public TotalTaxReportDto calculateTax(short year, MultipartFile ibReportFile, boolean isMilitary) {
    IBFilesParser.ParsedData parsedData = parser.parseIBFile(ibReportFile);

    var dividends = Optional.ofNullable(parsedData)
        .map(IBFilesParser.ParsedData::getDividends)
        .orElse(Collections.emptyList());

    var trades = Optional.ofNullable(parsedData)
        .map(IBFilesParser.ParsedData::getTrades)
        .orElse(Collections.emptyList());

    var withholdingTaxes = Optional.ofNullable(parsedData)
        .map(IBFilesParser.ParsedData::getWithholdingTaxes)
        .orElse(Collections.emptyList());

    var dividendTaxReports = getDividendTaxReports(isMilitary, dividends);
    var tradeTaxReports = getTradeTaxReports(isMilitary, trades);

    TotalTaxReport totalReport = totalTaxReportRepository.findByYear(year)
        .orElseGet(TotalTaxReport::new);
    totalReport.setYear(year);

    calculateTotals(totalReport, dividendTaxReports, tradeTaxReports, isMilitary);
    totalReport.setTaxReports(dividendTaxReports, tradeTaxReports);
    totalReport.setWithheldDividendTax(getWithheldTaxUah(withholdingTaxes));
    totalReport.setStatus(ReportStatus.CALCULATED);

    return taxReportMapper.toDto(totalTaxReportRepository.save(totalReport));
  }

  @Override
  @Transactional
  public byte[] generateUaTaxDeclarationXml(TaxDeclarationFormDto form) throws Exception {
    TotalTaxReport report = totalTaxReportRepository.findByYear(form.getYear())
        .orElseThrow(() ->
            new ResourceNotFoundException(String.format("No reports loaded for year %s", form.getYear())));

    UkrainianTaxOffice office = UkrainianTaxOffice.valueOf(form.getTaxOfficeId());

    UUID uuid = UUID.randomUUID();
    String mainFileName = String.format("F0100214_Zvit_%s.xml", uuid);
    String f1FileName = String.format("F0121214_DodatokF1_%s.xml", uuid);

    byte[] mainXml = xmlGeneratorService.generateDeclarBytes(createDeclar(uuid, report, form, office));
    byte[] f1Xml = xmlGeneratorService.generateDeclarBytes(createDeclarF1(uuid, report, form, office));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zip = new ZipOutputStream(baos)) {
      zip.putNextEntry(new ZipEntry(mainFileName));
      zip.write(mainXml);
      zip.closeEntry();
      zip.putNextEntry(new ZipEntry(f1FileName));
      zip.write(f1Xml);
      zip.closeEntry();
    }
    return baos.toByteArray();
  }

  @Override
  @Transactional(readOnly = true)
  public TaxYearSummaryDto getTaxYearSummary(short year) {
    TotalTaxReport report = totalTaxReportRepository.findByYear(year)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("No reports found for year %s", year)));

    List<SymbolDividendSummaryDto> dividendsBySymbol = report.getDividendTaxReports().stream()
        .collect(Collectors.groupingBy(DividendTaxReport::getSymbol))
        .values().stream()
        .map(taxReportMapper::toSymbolDividendSummaryDto)
        .sorted(Comparator.comparing(SymbolDividendSummaryDto::getSymbol))
        .toList();

    List<SymbolTradeSummaryDto> tradesBySymbol = report.getTradeTaxReports().stream()
        .collect(Collectors.groupingBy(TradeTaxReport::getSymbol))
        .values().stream()
        .map(taxReportMapper::toSymbolTradeSummaryDto)
        .sorted(Comparator.comparing(SymbolTradeSummaryDto::getSymbol))
        .toList();

    return taxReportMapper.toYearSummaryDto(report, dividendsBySymbol, tradesBySymbol);
  }

  private @NonNull List<DividendTaxReport> getDividendTaxReports(
      boolean isMilitary,
      List<IBFilesParser.DividendData> dividends
  ) {
    Set<LocalDate> uniqueDates = dividends.stream()
        .map(IBFilesParser.DividendData::getDate)
        .collect(Collectors.toSet());

    Map<LocalDate, BigDecimal> rateCache = new HashMap<>();
    for (LocalDate date : uniqueDates) {
      rateCache.put(date, exchangeRateService.getRateForDate(date));
    }

    return dividends.stream()
        .map(dividend -> taxReportMapper.toDividendTaxReport(dividend, rateCache.get(dividend.getDate()), isMilitary))
        .toList();
  }

  private List<TradeTaxReport> getTradeTaxReports(boolean isMilitary, List<IBFilesParser.TradeData> trades) {
    if (trades.isEmpty()) return Collections.emptyList();

    Set<LocalDate> allDates = trades.stream()
        .flatMap(t -> Stream.of(t.getTradeDate(), t.getOpenDate()).filter(Objects::nonNull))
        .collect(Collectors.toSet());

    Map<LocalDate, BigDecimal> rateCache = new HashMap<>();
    for (LocalDate date : allDates) {
      rateCache.put(date, exchangeRateService.getRateForDate(date));
    }

    return trades.stream()
        .filter(t -> t.getFifoPnlRealized() != null && t.getFifoPnlRealized().compareTo(BigDecimal.ZERO) > 0)
        .map(trade -> {
          BigDecimal nbuRateSell = rateCache.get(trade.getTradeDate());
          BigDecimal nbuRateBuy = trade.getOpenDate() != null ? rateCache.get(trade.getOpenDate()) : nbuRateSell;
          return taxReportMapper.toTradeTaxReport(trade, nbuRateSell, nbuRateBuy, isMilitary);
        })
        .toList();
  }

  private void calculateTotals(TotalTaxReport taxReport,
                               List<DividendTaxReport> dividendTaxReports,
                               List<TradeTaxReport> tradeTaxReports,
                               boolean isMilitary) {
    taxReport.setTradeTotalGain(sumTrades(tradeTaxReports, TradeTaxReport::getSellPrice));
    taxReport.setDividendTotalGain(sumDividends(dividendTaxReports, DividendTaxReport::getAmount));

    var sumTrades = sumTrades(tradeTaxReports, TradeTaxReport::getUaBrutto);
    var sumDividends = sumDividends(dividendTaxReports, DividendTaxReport::getUaBrutto);
    var totalUaBrutto = sumTrades.add(sumDividends);

    taxReport.setTradeTotalUaBrutto(sumTrades);
    taxReport.setDividendTotalUaBrutto(sumDividends);

    var totalTaxDividends = round(sumDividends.multiply(new BigDecimal("0.09")));
    var totalTaxTrades = round(sumTrades.multiply(new BigDecimal("0.18")));

    taxReport.setTradeTotalTax(totalTaxTrades);
    taxReport.setDividendTotalTax(totalTaxDividends);

    taxReport.setTradeTotalMilitaryTax5(sumTrades.multiply(new BigDecimal("0.05")));
    taxReport.setDividendTotalMilitaryTax5(sumDividends.multiply(new BigDecimal("0.05")));

    var totalMilitaryTax5 = round(totalUaBrutto.multiply(new BigDecimal("0.05")));
    taxReport.setTotalMilitaryTax5(totalMilitaryTax5);
    var totalTax = totalTaxDividends.add(totalTaxTrades);
    if (isMilitary) {
      taxReport.setTotalTaxSum(totalTax);
    } else {
      taxReport.setTotalTaxSum(round(totalTax.add(totalMilitaryTax5)));
    }
  }

  private BigDecimal getWithheldTaxUah(List<IBFilesParser.WithholdingTaxData> withholdingTaxes) {
    if (withholdingTaxes.isEmpty()) return BigDecimal.ZERO;

    Set<LocalDate> dates = withholdingTaxes.stream()
        .map(IBFilesParser.WithholdingTaxData::getDate)
        .collect(Collectors.toSet());

    Map<LocalDate, BigDecimal> rateCache = new HashMap<>();
    for (LocalDate date : dates) {
      rateCache.put(date, exchangeRateService.getRateForDate(date));
    }

    // Withholding amounts are negative (deducted from account); negate to get positive credit
    BigDecimal netUah = withholdingTaxes.stream()
        .map(wt -> wt.getAmount().multiply(rateCache.get(wt.getDate())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .negate();

    return netUah.max(BigDecimal.ZERO).setScale(6, RoundingMode.HALF_UP);
  }

  private BigDecimal sumDividends(List<DividendTaxReport> reports, Function<DividendTaxReport, BigDecimal> getter) {
    return reports.stream().map(getter).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal sumTrades(List<TradeTaxReport> reports, Function<TradeTaxReport, BigDecimal> getter) {
    return reports.stream().map(getter).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public List<TaxOfficeOptionDto> getTaxOffices() {
    return java.util.Arrays.stream(UkrainianTaxOffice.values())
        .map(o -> new TaxOfficeOptionDto(o.name(), o.getDisplayName()))
        .toList();
  }

  private static BigDecimal round(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private static String fmtDecl(BigDecimal value) {
    if (value == null) return "0.00";
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private Declar createDeclar(UUID uuid, TotalTaxReport report, TaxDeclarationFormDto form, UkrainianTaxOffice office) {
    String year = String.valueOf(form.getYear());
    String fillDate = LocalDate.now().format(FILL_DATE_FORMAT);

    DeclarHead head = new DeclarHead();
    head.setTin(form.getTin());
    head.setCDoc("F01");
    head.setCDocSub("002");
    head.setCDocVer("11");
    head.setCDocType("0");
    head.setCDocCnt("1");
    head.setCReg(office.getCReg());
    head.setCRaj(office.getCRaj());
    head.setPeriodMonth("12");
    head.setPeriodType("5");
    head.setPeriodYear(year);
    head.setCStiOrig(office.getCStiOrig());
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

    BigDecimal divBrutto = report.getDividendTotalUaBrutto();
    BigDecimal divTax = report.getDividendTotalTax();
    BigDecimal divMilitary = round(report.getDividendTotalMilitaryTax5());
    BigDecimal tradeBrutto = report.getTradeTotalUaBrutto();
    BigDecimal tradeTax = report.getTradeTotalTax();
    BigDecimal tradeMilitary = round(report.getTradeTotalMilitaryTax5());
    BigDecimal totalIncome = divBrutto.add(tradeBrutto);
    BigDecimal totalTax = divTax.add(tradeTax);
    BigDecimal totalMilitary = divMilitary.add(tradeMilitary);
    BigDecimal withheld = report.getWithheldDividendTax() != null
        ? report.getWithheldDividendTax() : BigDecimal.ZERO;
    BigDecimal netTax = totalTax.subtract(withheld).max(BigDecimal.ZERO);

    DeclarBody body = new DeclarBody();
    body.setH01("1");
    body.setH03("1");
    body.setH05("1");
    body.setHbos(form.getBuilding());
    body.setHcity(form.getCity());
    body.setHd1("1");
    body.setHfill(fillDate);
    body.setHname(form.getName());
    body.setHsti(office.getDisplayName());
    body.setHstreet(form.getStreet());
    body.setHtin(form.getTin());
    body.setHz("1");
    body.setHzy(year);
    body.setR0104g3(divBrutto);
    body.setR0104g6(divTax);
    body.setR0104g7(divMilitary);
    body.setR0108g3(tradeBrutto);
    body.setR0108g6(tradeTax);
    body.setR0108g7(tradeMilitary);
    body.setR01010g2s("Інші проценти");
    body.setR010g3(totalIncome);
    body.setR010g6(totalTax);
    body.setR010g7(totalMilitary);
    body.setR012g3(totalIncome);
    body.setR013g3(totalTax);
    body.setR018g3(withheld);
    body.setR0201g3(netTax);
    body.setR0211g3(totalMilitary);

    Declar declar = new Declar();
    declar.setDeclarHead(head);
    declar.setDeclarBody(body);
    return declar;
  }

  private Declar createDeclarF1(UUID uuid, TotalTaxReport report, TaxDeclarationFormDto form, UkrainianTaxOffice office) {
    String year = String.valueOf(form.getYear());
    String fillDate = LocalDate.now().format(FILL_DATE_FORMAT);

    DeclarHead head = new DeclarHead();
    head.setTin(form.getTin());
    head.setCDoc("F01");
    head.setCDocSub("212");
    head.setCDocVer("11");
    head.setCDocType("1");
    head.setCDocCnt("1");
    head.setCReg(office.getCReg());
    head.setCRaj(office.getCRaj());
    head.setPeriodMonth("12");
    head.setPeriodType("5");
    head.setPeriodYear(year);
    head.setCStiOrig(office.getCStiOrig());
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

    List<TradeTaxReport> trades = report.getTradeTaxReports();
    BigDecimal totalSell = BigDecimal.ZERO;
    BigDecimal totalBuy = BigDecimal.ZERO;

    DeclarBodyF1 body = new DeclarBodyF1();
    body.setHbos(form.getName());
    body.setHtin(form.getTin());
    body.setHz("1");
    body.setHzy(year);

    for (int i = 0; i < trades.size(); i++) {
      TradeTaxReport trade = trades.get(i);
      String rowNum = String.valueOf(i + 1);
      BigDecimal sell = trade.getUaPriceSell() != null ? trade.getUaPriceSell() : BigDecimal.ZERO;
      BigDecimal buy = trade.getUaPriceBuy() != null ? trade.getUaPriceBuy() : BigDecimal.ZERO;
      BigDecimal gain = trade.getUaBrutto() != null ? trade.getUaBrutto() : BigDecimal.ZERO;
      totalSell = totalSell.add(sell);
      totalBuy = totalBuy.add(buy);
      // G4 = sale proceeds (UAH), G5 = purchase cost (UAH), G6 = taxable gain
      body.addTableRow(rowNum,
          fmtDecl(trade.getQuantity()),
          trade.getSymbol(),
          fmtDecl(sell),
          fmtDecl(buy),
          fmtDecl(gain));
    }

    BigDecimal totalGain = report.getTradeTotalUaBrutto();
    BigDecimal tradeTax = report.getTradeTotalTax();
    BigDecimal tradeMilitary = round(report.getTradeTotalMilitaryTax5());

    body.setR001g4(fmtDecl(totalSell));
    body.setR001g5(fmtDecl(totalBuy));
    body.setR001g6(fmtDecl(totalGain));
    body.setR003g6(fmtDecl(totalGain));
    body.setR004g6(fmtDecl(tradeTax));
    body.setR005g6(fmtDecl(tradeMilitary));
    body.setR031g6(fmtDecl(totalGain));
    body.setR042g6(fmtDecl(tradeTax));
    body.setR052g6(fmtDecl(tradeMilitary));

    Declar declar = new Declar();
    declar.setDeclarHead(head);
    declar.setDeclarBody(body);
    return declar;
  }
}
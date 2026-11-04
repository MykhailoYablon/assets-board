package com.assets.board.mapper;

import com.assets.board.dto.DividendTaxReportDto;
import com.assets.board.dto.SymbolDividendSummaryDto;
import com.assets.board.dto.SymbolTradeSummaryDto;
import com.assets.board.dto.TaxYearSummaryDto;
import com.assets.board.dto.TotalTaxReportDto;
import com.assets.board.dto.TradeTaxReportDto;
import com.assets.board.entity.DividendTaxReport;
import com.assets.board.entity.TotalTaxReport;
import com.assets.board.entity.TradeTaxReport;
import com.assets.board.service.IBFilesParser;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface TaxReportMapper {

    @Mapping(source = "dividendTaxReports", target = "dividendTaxReportDtos")
    @Mapping(source = "tradeTaxReports", target = "tradeTaxReportDtos")
    TotalTaxReportDto toDto(TotalTaxReport report);

    DividendTaxReportDto toDividendDto(DividendTaxReport report);

    TradeTaxReportDto toTradeDto(TradeTaxReport report);

    @Mapping(source = "report.year", target = "year")
    @Mapping(source = "report.dividendTotalGain", target = "dividendAmount")
    @Mapping(source = "report.dividendTotalUaBrutto", target = "dividendUaBrutto")
    @Mapping(source = "report.dividendTotalTax", target = "dividendTax")
    @Mapping(source = "report.dividendTotalMilitaryTax5", target = "dividendMilitary")
    @Mapping(target = "dividendTaxSum", expression = "java(report.getDividendTotalTax().add(report.getDividendTotalMilitaryTax5()))")
    @Mapping(source = "report.tradeTotalUaBrutto", target = "tradeUaBrutto")
    @Mapping(source = "report.tradeTotalTax", target = "tradeTax")
    @Mapping(source = "report.tradeTotalMilitaryTax5", target = "tradeMilitary")
    @Mapping(target = "tradeTaxSum", expression = "java(report.getTradeTotalTax().add(report.getTradeTotalMilitaryTax5()))")
    @Mapping(source = "report.totalTaxSum", target = "totalTaxSum")
    @Mapping(target = "withheldDividendTax", expression = "java(report.getWithheldDividendTax() != null ? report.getWithheldDividendTax() : java.math.BigDecimal.ZERO)")
    @Mapping(source = "dividendsBySymbol", target = "dividendsBySymbol")
    @Mapping(source = "tradesBySymbol", target = "tradesBySymbol")
    TaxYearSummaryDto toYearSummaryDto(TotalTaxReport report, List<SymbolDividendSummaryDto> dividendsBySymbol, List<SymbolTradeSummaryDto> tradesBySymbol);

    @Mapping(source = "dividend.symbol", target = "symbol")
    @Mapping(source = "dividend.date", target = "date")
    @Mapping(source = "dividend.amount", target = "amount")
    @Mapping(source = "exchangeRate", target = "nbuRate")
    @Mapping(target = "uaBrutto", ignore = true)
    @Mapping(target = "tax9", ignore = true)
    @Mapping(target = "militaryTax5", ignore = true)
    @Mapping(target = "taxSum", ignore = true)
    @Mapping(target = "totalTaxReport", ignore = true)
    @Mapping(target = "id", ignore = true)
    DividendTaxReport toDividendTaxReport(IBFilesParser.DividendData dividend, BigDecimal exchangeRate, @Context boolean isMilitary);

    @AfterMapping
    default void computeDividendTaxFields(@MappingTarget DividendTaxReport report, @Context boolean isMilitary) {
        BigDecimal uaBrutto = report.getNbuRate().multiply(report.getAmount()).setScale(2, RoundingMode.HALF_DOWN);
        report.setUaBrutto(uaBrutto);
        BigDecimal tax9 = uaBrutto.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal militaryTax5 = uaBrutto.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_DOWN);
        report.setTax9(tax9);
        report.setMilitaryTax5(militaryTax5);
        report.setTaxSum(isMilitary ? tax9 : tax9.add(militaryTax5));
    }

    @Mapping(source = "trade.symbol", target = "symbol")
    @Mapping(source = "trade.quantity", target = "quantity")
    @Mapping(source = "trade.tradeDate", target = "sellDate")
    @Mapping(target = "buyDate", expression = "java(trade.getOpenDate() != null ? trade.getOpenDate() : trade.getTradeDate())")
    @Mapping(source = "trade.tradePrice", target = "originalPrice")
    @Mapping(source = "trade.fifoPnlRealized", target = "sellPrice")
    @Mapping(source = "nbuRateSell", target = "nbuRateSell")
    @Mapping(source = "nbuRateBuy", target = "nbuRateBuy")
    @Mapping(target = "uaPriceSell", ignore = true)
    @Mapping(target = "uaPriceBuy", ignore = true)
    @Mapping(target = "uaBrutto", ignore = true)
    @Mapping(target = "tax18", ignore = true)
    @Mapping(target = "militaryTax5", ignore = true)
    @Mapping(target = "taxSum", ignore = true)
    @Mapping(target = "isMilitary", ignore = true)
    @Mapping(target = "totalTaxReport", ignore = true)
    @Mapping(target = "id", ignore = true)
    TradeTaxReport toTradeTaxReport(IBFilesParser.TradeData trade, BigDecimal nbuRateSell, BigDecimal nbuRateBuy, @Context boolean isMilitary);

    @AfterMapping
    default void computeTradeTaxFields(@MappingTarget TradeTaxReport report, IBFilesParser.TradeData trade, @Context boolean isMilitary) {
        BigDecimal uaSell = trade.getProceeds().add(trade.getTradePrice()).multiply(report.getNbuRateSell()).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal uaBuy = trade.getTradePrice().multiply(report.getNbuRateBuy()).setScale(2, RoundingMode.HALF_DOWN);
        report.setUaPriceSell(uaSell);
        report.setUaPriceBuy(uaBuy);
        BigDecimal uaBrutto = uaSell.subtract(uaBuy).max(BigDecimal.ZERO);
        report.setUaBrutto(uaBrutto);
        BigDecimal tax18 = uaBrutto.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal militaryTax5 = uaBrutto.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_DOWN);
        report.setTax18(tax18);
        report.setMilitaryTax5(militaryTax5);
        report.setTaxSum(isMilitary ? tax18 : tax18.add(militaryTax5));
        report.setIsMilitary(isMilitary);
    }

    default SymbolDividendSummaryDto toSymbolDividendSummaryDto(List<DividendTaxReport> rows) {
        SymbolDividendSummaryDto dto = new SymbolDividendSummaryDto();
        dto.setSymbol(rows.get(0).getSymbol());
        dto.setTotalAmount(sum(rows, DividendTaxReport::getAmount));
        dto.setTotalUaBrutto(sum(rows, DividendTaxReport::getUaBrutto));
        dto.setTotalTax9(sum(rows, DividendTaxReport::getTax9));
        dto.setTotalMilitaryTax5(sum(rows, DividendTaxReport::getMilitaryTax5));
        dto.setTotalTaxSum(sum(rows, DividendTaxReport::getTaxSum));
        dto.setPayments(rows.stream()
                .sorted(Comparator.comparing(DividendTaxReport::getDate))
                .map(this::toDividendDto)
                .toList());
        return dto;
    }

    default SymbolTradeSummaryDto toSymbolTradeSummaryDto(List<TradeTaxReport> rows) {
        SymbolTradeSummaryDto dto = new SymbolTradeSummaryDto();
        dto.setSymbol(rows.get(0).getSymbol());
        dto.setTotalUaBrutto(sum(rows, TradeTaxReport::getUaBrutto));
        dto.setTotalTax(sum(rows, TradeTaxReport::getTax18));
        dto.setTotalMilitary(sum(rows, TradeTaxReport::getMilitaryTax5));
        dto.setTotalTaxSum(sum(rows, TradeTaxReport::getTaxSum));
        dto.setTrades(rows.stream()
                .sorted(Comparator.comparing(TradeTaxReport::getSellDate))
                .map(this::toTradeDto)
                .toList());
        return dto;
    }

    private static <T> BigDecimal sum(List<T> items, Function<T, BigDecimal> getter) {
        return items.stream().map(getter).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

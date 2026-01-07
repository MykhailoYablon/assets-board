package com.assets.board.service;

import com.assets.board.client.NbuDataClient;
import com.assets.board.entity.ExchangeRate;
import com.assets.board.repository.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@AllArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository repository;
    private final NbuDataClient nbuDataClient;

    @Transactional
    public BigDecimal getRateForDate(LocalDate date) {
        return repository.findByDate(date)
                .map(ExchangeRate::getNbuRate)
                .orElseGet(() -> fetchAndSaveRate(date));
    }

    @Transactional
    public ExchangeRate saveExchangeRate(LocalDate date, BigDecimal rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setDate(date);
        exchangeRate.setNbuRate(rate);

        return repository.save(exchangeRate);
    }

    private BigDecimal fetchAndSaveRate(LocalDate date) {
        BigDecimal exchangeRate = nbuDataClient.exchangeRate(date);
        ExchangeRate rate = this.saveExchangeRate(date, exchangeRate);
        return rate.getNbuRate();
    }
}

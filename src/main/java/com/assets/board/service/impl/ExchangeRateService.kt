package com.assets.board.service.impl

import com.assets.board.client.NbuDataClient
import com.assets.board.entity.ExchangeRate
import com.assets.board.repository.ExchangeRateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional
class ExchangeRateService(
    private val repository: ExchangeRateRepository,
    private val nbuDataClient: NbuDataClient
) {
    fun getRateForDate(date: LocalDate?): BigDecimal? {
        return repository.findByDate(date)?.nbuRate
            ?: date?.let { fetchAndSaveRate(it) }
    }

    fun saveExchangeRate(date: LocalDate, rate: BigDecimal): ExchangeRate {
        val exchangeRate = ExchangeRate(
            date = date,
            nbuRate = rate
        )
        return repository.save(exchangeRate)
    }

    private fun fetchAndSaveRate(date: LocalDate): BigDecimal? {
        val exchangeRate = nbuDataClient.exchangeRate(date)
        return saveExchangeRate(date, exchangeRate).nbuRate
    }
}

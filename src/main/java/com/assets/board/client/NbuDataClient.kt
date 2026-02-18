package com.assets.board.client

import com.assets.board.model.NbuExchangeRate
import com.assets.board.model.enums.Endpoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import java.util.function.Function

@Component("NbuData")
@Qualifier("NbuData")
@Primary
class NbuDataClient {
    @Autowired
    @Qualifier("nbuClient")
    private val nbuClient: RestClient? = null

    fun exchangeRate(date: LocalDate): BigDecimal {
        val requestDate = date.toString().replace("-".toRegex(), "")
        val rate = nbuClient!!.get()
            .uri(
                (Endpoint.NBU_EXCHANGE.url() + "?json"
                        + "&valcode=" + "USD"
                        + "&date=" + requestDate)
            )
            .retrieve()
            .body<MutableList<NbuExchangeRate?>?>(object :
                ParameterizedTypeReference<MutableList<NbuExchangeRate?>?>() {
            })
        return Optional.ofNullable(rate)
            .map { it.firstOrNull() }  // Use Kotlin's firstOrNull()
            .map { it?.rate }
            .map { BigDecimal(it) }
            .orElse(BigDecimal.ZERO)
    }
}

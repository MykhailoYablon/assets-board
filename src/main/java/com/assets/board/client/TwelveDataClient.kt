package com.assets.board.client

import com.assets.board.model.enums.Endpoint
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.*

@NoArgsConstructor
@Getter
@Setter
@Component("twelveData")
@Qualifier("twelveData")
@Primary
class TwelveDataClient {
    @Autowired
    @Qualifier("twelveClient")
    private val twelveClient: RestClient? = null

    @Value("\${financial.twelve.api.token}")
    private val token: String? = null

    fun price(symbol: String): String? {
        val price = twelveClient!!.get()
            .uri(
                (Endpoint.TWELVE_PRICE.url() + "?apikey=" + token
                        + "&symbol=" + symbol.uppercase(Locale.getDefault()))
            )
            .retrieve()
            .body(String::class.java)
        return price
    }
}

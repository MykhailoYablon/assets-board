package com.assets.board.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.assets.board.model.enums.Endpoint.TWELVE_PRICE;

@NoArgsConstructor
@Getter
@Setter
@Component("twelveData")
@Qualifier("twelveData")
@Slf4j
@Primary
public class TwelveDataClient {


    @Autowired
    @Qualifier("twelveClient")
    private RestClient twelveClient;
    @Value("${financial.twelve.api.token}")
    private String token;

    public String price(String symbol) {
        String price = twelveClient.get()
                .uri(TWELVE_PRICE.url() + "?apikey=" + token
                        + "&symbol=" + symbol.toUpperCase()
                )
                .retrieve()
                .body(String.class);
        log.info("Latest price for symbol - {} is {}", symbol, price);
        return price;
    }

}

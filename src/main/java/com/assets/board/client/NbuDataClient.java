package com.assets.board.client;

import com.assets.board.model.NbuExchangeRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.assets.board.model.enums.Endpoint.NBU_EXCHANGE;

@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Component("NbuData")
@Qualifier("NbuData")
@Primary
public class NbuDataClient {

    @Autowired
    @Qualifier("nbuClient")
    private RestClient nbuClient;

    public String exchangeRate(String date, String currency) {
        List<NbuExchangeRate> rate = nbuClient.get()
                .uri(NBU_EXCHANGE.url() + "?json"
                        + "&valcode=" + currency
                        + "&date=" + date
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("Latest exchange rate for date - {} is {}", date, rate);
        return rate.getFirst().getRate();
    }

}

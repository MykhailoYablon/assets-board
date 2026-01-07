package com.assets.board.client;

import com.assets.board.model.NbuExchangeRate;
import com.assets.board.service.ExchangeRateService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public BigDecimal exchangeRate(LocalDate date) {
        var requestDate = date.toString().replaceAll("-", "");
        List<NbuExchangeRate> rate = nbuClient.get()
                .uri(NBU_EXCHANGE.url() + "?json"
                        + "&valcode=" + "USD"
                        + "&date=" + requestDate
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        BigDecimal exchangeRate = Optional.ofNullable(rate)
                .map(List::getFirst)
                .map(NbuExchangeRate::getRate)
                .map(BigDecimal::new)
                .orElse(BigDecimal.ZERO);
        return exchangeRate;
    }
}

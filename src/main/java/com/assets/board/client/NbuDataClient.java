package com.assets.board.client;

import com.assets.board.model.NbuExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.assets.board.model.enums.Endpoint.NBU_EXCHANGE;

@Slf4j
@Component
public class NbuDataClient {

    private final RestClient nbuClient;

    public NbuDataClient(@Qualifier("nbuClient") RestClient nbuClient) {
        this.nbuClient = nbuClient;
    }

    public BigDecimal exchangeRate(LocalDate date) {
        String requestDate = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        List<NbuExchangeRate> rate = nbuClient.get()
                .uri(NBU_EXCHANGE.url() + "?json&valcode=USD&date=" + requestDate)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return Optional.ofNullable(rate)
                .map(List::getFirst)
                .map(NbuExchangeRate::getRate)
                .map(BigDecimal::new)
                .orElse(BigDecimal.ZERO);
    }
}

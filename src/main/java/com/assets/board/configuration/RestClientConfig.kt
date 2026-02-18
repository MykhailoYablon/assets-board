package com.assets.board.configuration

import com.assets.board.configuration.interceptor.RateLimitInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
open class RestClientConfig {
    @Bean
    @Qualifier("twelveClient")
    open fun twelveClient(@Qualifier("rateLimitInterceptor") interceptor: ClientHttpRequestInterceptor): RestClient {
        return RestClient.builder()
            .baseUrl("https://api.twelvedata.com") // Your API base URL
            .requestInterceptor(interceptor)
            .build()
    }

    @Bean
    @Qualifier("nbuClient")
    open fun nbuClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://bank.gov.ua") // Your API base URL
            .build()
    }

    @Bean
    @Qualifier("rateLimitInterceptor")
    open fun rateLimitInterceptor(): ClientHttpRequestInterceptor {
        return RateLimitInterceptor(4, Duration.ofMinutes(1))
    }
}

package com.assets.board.configuration

import com.google.genai.Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GeminiConfig {
    @Bean
    open fun geminiClient(): Client {
        return Client()
    }
}

package com.assets.board.service;

import com.assets.board.entity.Position;
import com.assets.board.repository.PositionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class AIService {

    private final PositionRepository positionRepository;
    private final Client client;
    private final ObjectMapper objectMapper;

    public String analyzePositions() {
        List<Position> positions = positionRepository.findAll();
        //add analyze of holdings based on ai response
        // The client gets the API key from the environment variable `GEMINI_API_KEY`.
        GenerateContentResponse response;
        try {
            response = client.models.generateContent(
                    "gemini-2.5-flash",
                    """
                            Imagine you are a financial advisor with 50 years of experience.
                            Conduct an analysis of the current portfolio provided in JSON format.
                            In your analysis, consider the key risks arising from the current asset allocation and market conditions,
                            as well as potential issues that may impact the portfolio's performance.
                            Analyze different types of profitability and unrealized P&L, perform comparisons, and provide recommendations.
                            Identify promising or underutilized opportunities that could be beneficial in the future,
                            considering current and projected market trends.
                            Provide practical recommendations for rebalancing the portfolio and suggest growth strategies
                            for both the mid-term and short-term. The report should be concise (up to 700 words)
                            and written in clear and accessible language so that both financial professionals and individuals
                            with limited financial knowledge can easily understand it.
                            """ + objectMapper.writeValueAsString(positions),
                    null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Review is done!");
        return response.text();
    }
}

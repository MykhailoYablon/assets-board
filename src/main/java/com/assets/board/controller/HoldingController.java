package com.assets.board.controller;

import com.assets.board.client.TwelveDataClient;
import com.assets.board.model.ai.Holding;
import com.assets.board.service.HoldingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/holdings")
@AllArgsConstructor
public class HoldingController {
    private final HoldingService holdingService;

    private final TwelveDataClient twelveDataClient;
    private final Client client;
    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public List<Holding> getAllHoldings() {
        return holdingService.getAllHoldings();
    }

    @GetMapping("/ai")
    public String getAiSummary() {
        Holding holding;
        try {
            holding = objectMapper.readValue(new File("src/main/resources/holdings.json"),
                    Holding.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        holding.getPositions().forEach(position -> {
            //read last price
            String currentPrice = twelveDataClient.price(position.getSymbol());
            position.setCurrentPrice(currentPrice);
        });

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
                            """ + objectMapper.writeValueAsString(holding),
                    null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(response.text());
        return "Doing Great!";
    }

}
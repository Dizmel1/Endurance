package com.example.Endurance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class MarketDataService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${market.alpha-vantage.api-key:}")
    private String alphaVantageApiKey;

    public BigDecimal getCurrencyRate(String from, String to) {
        try {
            if (alphaVantageApiKey == null || alphaVantageApiKey.isBlank()) {
                throw new RuntimeException("Не указан API-ключ Alpha Vantage");
            }

            String url = "https://www.alphavantage.co/query"
                    + "?function=CURRENCY_EXCHANGE_RATE"
                    + "&from_currency=" + from
                    + "&to_currency=" + to
                    + "&apikey=" + alphaVantageApiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("Ошибка получения курса валют. HTTP status: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());

            if (root.has("Note")) {
                throw new RuntimeException("Alpha Vantage вернул ограничение по количеству запросов: " + root.get("Note").asText());
            }

            if (root.has("Information")) {
                throw new RuntimeException("Alpha Vantage вернул сообщение: " + root.get("Information").asText());
            }

            JsonNode rateNode = root
                    .path("Realtime Currency Exchange Rate")
                    .path("5. Exchange Rate");

            if (rateNode.isMissingNode() || rateNode.asText().isBlank()) {
                throw new RuntimeException("Курс валют не найден в ответе Alpha Vantage: " + from + "/" + to
                        + ". Ответ API: " + response.body());
            }

            return new BigDecimal(rateNode.asText()).setScale(6, RoundingMode.HALF_UP);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить курс " + from + "/" + to + ": " + e.getMessage(), e);
        }
    }

    public BigDecimal getStockPriceUsd(String symbol) {
        try {
            if (alphaVantageApiKey == null || alphaVantageApiKey.isBlank()) {
                throw new RuntimeException("Не указан API-ключ Alpha Vantage");
            }

            String url = "https://www.alphavantage.co/query"
                    + "?function=GLOBAL_QUOTE"
                    + "&symbol=" + symbol
                    + "&apikey=" + alphaVantageApiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("Ошибка получения цены акции. HTTP status: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());

            if (root.has("Note")) {
                throw new RuntimeException("Alpha Vantage вернул ограничение по количеству запросов: " + root.get("Note").asText());
            }

            if (root.has("Information")) {
                throw new RuntimeException("Alpha Vantage вернул сообщение: " + root.get("Information").asText());
            }

            JsonNode priceNode = root.path("Global Quote").path("05. price");

            if (priceNode.isMissingNode() || priceNode.asText().isBlank()) {
                throw new RuntimeException("Цена акции не найдена: " + symbol);
            }

            return new BigDecimal(priceNode.asText()).setScale(6, RoundingMode.HALF_UP);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить цену акции: " + symbol, e);
        }
    }

//    public BigDecimal getStockPriceRub(String symbol) {
//        BigDecimal stockPriceUsd = getStockPriceUsd(symbol);
//        BigDecimal usdRubRate = getCurrencyRate("USD", "RUB");
//
//        return stockPriceUsd
//                .multiply(usdRubRate)
//                .setScale(6, RoundingMode.HALF_UP);
//    }
}

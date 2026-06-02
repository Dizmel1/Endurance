package com.example.Endurance.config;

import com.example.Endurance.asset.AssetCategoryEntity;
import com.example.Endurance.asset.AssetCategoryRepository;
import com.example.Endurance.asset.AssetEntity;
import com.example.Endurance.asset.AssetRepository;
import com.example.Endurance.quote.QuoteEntity;
import com.example.Endurance.quote.QuoteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetRepository assetRepository;
    private final QuoteRepository quoteRepository;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataInitializer(AssetCategoryRepository assetCategoryRepository,
                           AssetRepository assetRepository,
                           QuoteRepository quoteRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
        this.assetRepository = assetRepository;
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void run(String... args) {
        AssetCategoryEntity currencyCategory = assetCategoryRepository
                .findByName("Валюты")
                .orElseGet(() -> {
                    AssetCategoryEntity category = new AssetCategoryEntity();
                    category.setName("Валюты");
                    return assetCategoryRepository.save(category);
                });

        BigDecimal usdRubRate = loadRate("USD", "RUB", new BigDecimal("90.000000"));
        BigDecimal eurRubRate = loadRate("EUR", "RUB", new BigDecimal("98.000000"));

        createOrUpdateAsset(
                currencyCategory,
                "USD/RUB",
                "Доллар США к российскому рублю",
                usdRubRate,
                "RUB"
        );

        createOrUpdateAsset(
                currencyCategory,
                "EUR/RUB",
                "Евро к российскому рублю",
                eurRubRate,
                "RUB"
        );
    }

    private void createOrUpdateAsset(AssetCategoryEntity category,
                                     String ticker,
                                     String name,
                                     BigDecimal price,
                                     String currency) {

        if (price == null) {
            if ("USD/RUB".equals(ticker)) {
                price = new BigDecimal("90.000000");
            } else if ("EUR/RUB".equals(ticker)) {
                price = new BigDecimal("98.000000");
            } else {
                price = BigDecimal.ONE;
            }
        }

        BigDecimal finalPrice = price;

        AssetEntity asset = assetRepository.findByTicker(ticker)
                .orElseGet(() -> {
                    AssetEntity newAsset = new AssetEntity();
                    newAsset.setCategory(category);
                    newAsset.setTicker(ticker);
                    newAsset.setName(name);
                    newAsset.setStartBalance(finalPrice);
                    newAsset.setCurrency(currency);
                    newAsset.setActive(true);
                    return assetRepository.save(newAsset);
                });

        asset.setStartBalance(finalPrice);
        asset.setCurrency(currency);
        asset.setActive(true);
        assetRepository.save(asset);

        QuoteEntity quote = new QuoteEntity();
        quote.setAsset(asset);
        quote.setTs(LocalDateTime.now());
        quote.setPrice(finalPrice);
        quoteRepository.save(quote);
    }

    private BigDecimal loadRate(String from, String to, BigDecimal fallback) {
        try {
            String url = "https://api.frankfurter.app/latest?from=" + from + "&to=" + to;

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
                return fallback;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode rateNode = root.path("rates").path(to);

            if (rateNode.isMissingNode() || !rateNode.isNumber()) {
                return fallback;
            }

            return rateNode.decimalValue().setScale(6, java.math.RoundingMode.HALF_UP);

        } catch (Exception e) {
            return fallback;
        }
    }
}
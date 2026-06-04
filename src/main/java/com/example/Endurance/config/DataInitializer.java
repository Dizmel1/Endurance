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

        BigDecimal usdRubRate = loadRate("USD", "RUB", new BigDecimal("74.347796"));
        BigDecimal eurRubRate = loadRate("EUR", "RUB", new BigDecimal("86.567272"));

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

        AssetCategoryEntity stockCategory = assetCategoryRepository
                .findByName("Акции")
                .orElseGet(() -> {
                    AssetCategoryEntity category = new AssetCategoryEntity();
                    category.setName("Акции");
                    return assetCategoryRepository.save(category);
                });

        createOrUpdateAsset(
                stockCategory,
                "AAPL",
                "Apple Inc.",
                new BigDecimal("27923.400000"),
                "RUB"
        );

        createOrUpdateAsset(
                stockCategory,
                "MSFT",
                "Microsoft Corporation",
                new BigDecimal("38460.600000"),
                "RUB"
        );

        createOrUpdateAsset(
                stockCategory,
                "TSLA",
                "Tesla Inc.",
                new BigDecimal("38133.000000"),
                "RUB"
        );

        AssetCategoryEntity commodityCategory = assetCategoryRepository
                .findByName("Сырьевые активы")
                .orElseGet(() -> {
                    AssetCategoryEntity category = new AssetCategoryEntity();
                    category.setName("Сырьевые активы");
                    return assetCategoryRepository.save(category);
                });

        createOrUpdateAsset(
                commodityCategory,
                "GOLD",
                "Золото",
                new BigDecimal("10500.000000"),
                "RUB"
        );

        createOrUpdateAsset(
                commodityCategory,
                "SILVER",
                "Серебро",
                new BigDecimal("120.000000"),
                "RUB"
        );

        createOrUpdateAsset(
                commodityCategory,
                "OIL",
                "Нефть Brent",
                new BigDecimal("7200.000000"),
                "RUB"
        );
    }

    private void createOrUpdateAsset(AssetCategoryEntity category,
                                     String ticker,
                                     String name,
                                     BigDecimal price,
                                     String currency) {

        AssetEntity asset = assetRepository.findByTicker(ticker)
                .orElseGet(() -> {
                    AssetEntity newAsset = new AssetEntity();
                    newAsset.setCategory(category);
                    newAsset.setTicker(ticker);
                    newAsset.setName(name);
                    newAsset.setStartBalance(price);
                    newAsset.setCurrency(currency);
                    newAsset.setActive(true);
                    return assetRepository.save(newAsset);
                });

        asset.setCategory(category);
        asset.setName(name);
        asset.setCurrency(currency);
        asset.setActive(true);

        if (asset.getStartBalance() == null) {
            asset.setStartBalance(price);
        }

        assetRepository.save(asset);

        boolean quoteExists = quoteRepository
                .findTopByAsset_TickerOrderByTsDesc(ticker)
                .isPresent();

        if (!quoteExists) {
            QuoteEntity quote = new QuoteEntity();
            quote.setAsset(asset);
            quote.setTs(LocalDateTime.now());
            quote.setPrice(price);
            quoteRepository.save(quote);
        }
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
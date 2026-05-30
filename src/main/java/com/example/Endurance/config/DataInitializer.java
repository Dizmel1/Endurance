package com.example.Endurance.config;

import com.example.Endurance.asset.AssetCategoryEntity;
import com.example.Endurance.asset.AssetCategoryRepository;
import com.example.Endurance.asset.AssetEntity;
import com.example.Endurance.asset.AssetRepository;
import com.example.Endurance.quote.QuoteEntity;
import com.example.Endurance.quote.QuoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetRepository assetRepository;
    private final QuoteRepository quoteRepository;

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

        createAssetIfNotExists(
                currencyCategory,
                "USD/RUB",
                "Доллар США к российскому рублю",
                new BigDecimal("90.000000"),
                "RUB"
        );

        createAssetIfNotExists(
                currencyCategory,
                "EUR/RUB",
                "Евро к российскому рублю",
                new BigDecimal("98.000000"),
                "RUB"
        );
    }

    private void createAssetIfNotExists(AssetCategoryEntity category,
                                        String ticker,
                                        String name,
                                        BigDecimal startPrice,
                                        String currency) {
        AssetEntity asset = assetRepository.findByTicker(ticker)
                .orElseGet(() -> {
                    AssetEntity newAsset = new AssetEntity();
                    newAsset.setCategory(category);
                    newAsset.setTicker(ticker);
                    newAsset.setName(name);
                    newAsset.setStartBalance(startPrice);
                    newAsset.setCurrency(currency);
                    newAsset.setActive(true);
                    return assetRepository.save(newAsset);
                });

        boolean hasQuote = quoteRepository.findTopByAsset_TickerOrderByTsDesc(ticker).isPresent();

        if (!hasQuote) {
            QuoteEntity quote = new QuoteEntity();
            quote.setAsset(asset);
            quote.setTs(LocalDateTime.now());
            quote.setPrice(startPrice);
            quoteRepository.save(quote);
        }
    }
}
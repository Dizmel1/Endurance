package com.example.Endurance.quote;

import com.example.Endurance.asset.AssetEntity;
import com.example.Endurance.asset.AssetRepository;
import com.example.Endurance.service.MarketDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class QuoteUpdateService {

    private final AssetRepository assetRepository;
    private final QuoteRepository quoteRepository;
    private final MarketDataService marketDataService;

    public QuoteUpdateService(AssetRepository assetRepository,
                              QuoteRepository quoteRepository,
                              MarketDataService marketDataService) {
        this.assetRepository = assetRepository;
        this.quoteRepository = quoteRepository;
        this.marketDataService = marketDataService;
    }

    @Transactional
    public QuoteEntity updateCurrencyQuote(String ticker, String from, String to) {
        AssetEntity asset = assetRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("Актив не найден: " + ticker));

        BigDecimal price = marketDataService.getCurrencyRate(from, to);

        return saveQuote(asset, price);
    }

    @Transactional
    public QuoteEntity updateStockQuote(String ticker) {
        AssetEntity asset = assetRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("Актив не найден: " + ticker));

        BigDecimal stockPriceUsd = marketDataService.getStockPriceUsd(ticker);

        AssetEntity usdRubAsset = assetRepository.findByTicker("USD/RUB")
                .orElseThrow(() -> new RuntimeException("Актив USD/RUB не найден"));

        BigDecimal usdRubRate = quoteRepository
                .findTopByAsset_TickerOrderByTsDesc("USD/RUB")
                .map(QuoteEntity::getPrice)
                .orElse(usdRubAsset.getStartBalance());

        BigDecimal priceRub = stockPriceUsd
                .multiply(usdRubRate)
                .setScale(6, java.math.RoundingMode.HALF_UP);

        return saveQuote(asset, priceRub);
    }

    private QuoteEntity saveQuote(AssetEntity asset, BigDecimal price) {
        asset.setStartBalance(price);
        assetRepository.save(asset);

        QuoteEntity quote = new QuoteEntity();
        quote.setAsset(asset);
        quote.setTs(LocalDateTime.now());
        quote.setPrice(price);

        return quoteRepository.save(quote);
    }
}
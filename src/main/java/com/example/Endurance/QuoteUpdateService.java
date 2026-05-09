package com.example.Endurance;

import com.example.Endurance.asset.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Endurance.asset.AssetEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class QuoteUpdateService {

    private final CurrencyRateClient currencyRateClient;
    private final AssetRepository assetRepository;
    private final QuoteRepository quoteRepository;

    public QuoteUpdateService(
            CurrencyRateClient currencyRateClient,
            AssetRepository assetRepository,
            QuoteRepository quoteRepository
    ) {
        this.currencyRateClient = currencyRateClient;
        this.assetRepository = assetRepository;
        this.quoteRepository = quoteRepository;
    }

    @Transactional
    public void updateCurrencyQuote(String ticker, String from, String to) {
        AssetEntity asset = assetRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("Актив не найден: " + ticker));

        BigDecimal rate = currencyRateClient.getRate(from, to);

        QuoteEntity quote = new QuoteEntity();
        quote.setAsset(asset);
        quote.setTs(LocalDateTime.now());
        quote.setPrice(rate);

        quoteRepository.save(quote);
    }
}

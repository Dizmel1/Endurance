package com.example.Endurance.quote;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QuoteScheduler {

    private final QuoteUpdateService quoteUpdateService;

    public QuoteScheduler(QuoteUpdateService quoteUpdateService) {
        this.quoteUpdateService = quoteUpdateService;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateUsdRubQuotes() {
        quoteUpdateService.updateCurrencyQuote("USD/RUB", "USD", "RUB");
        quoteUpdateService.updateCurrencyQuote("EUR/USD", "EUR", "USD");
        quoteUpdateService.updateCurrencyQuote("EUR/RUB", "EUR", "RUB");
    }
}

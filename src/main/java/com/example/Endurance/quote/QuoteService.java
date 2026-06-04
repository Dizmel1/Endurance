package com.example.Endurance.quote;

import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public QuoteResponse getLatestQuoteByAssetId(Long assetId) {
        QuoteEntity quote = quoteRepository.findTopByAsset_IdOrderByTsDesc(assetId)
                .orElseThrow(() -> new RuntimeException("Курс для актива не найден"));

        return toResponse(quote);
    }

    public QuoteResponse getLatestQuoteByTicker(String ticker) {
        QuoteEntity quote = quoteRepository.findTopByAsset_TickerOrderByTsDesc(ticker)
                .orElseThrow(() -> new RuntimeException("Курс для тикера не найден: " + ticker));

        return toResponse(quote);
    }

    private QuoteResponse toResponse(QuoteEntity quote) {
        return new QuoteResponse(
                quote.getId(),
                quote.getAsset().getId(),
                quote.getAsset().getTicker(),
                quote.getAsset().getName(),
                quote.getAsset().getCurrency(),
                quote.getPrice(),
                quote.getTs()
        );
    }
}
package com.example.Endurance.quote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteUpdateService quoteUpdateService;
    private final QuoteRepository quoteRepository;
    private final QuoteService quoteService;

    public QuoteController(QuoteUpdateService quoteUpdateService, QuoteRepository quoteRepository, QuoteService quoteService) {
        this.quoteUpdateService = quoteUpdateService;
        this.quoteRepository = quoteRepository;
        this.quoteService = quoteService;
    }

    @GetMapping("/latest/asset/{assetId}")
    public QuoteResponse getLatestQuoteByAssetId(@PathVariable Long assetId) {
        QuoteEntity quote = quoteRepository.findTopByAsset_IdOrderByTsDesc(assetId)
                .orElseThrow(() -> new RuntimeException("Котировка не найдена"));

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

    @GetMapping("/history/asset/{assetId}")
    public List<QuoteResponse> getQuoteHistoryByAssetId(@PathVariable Long assetId) {
        return quoteRepository.findTop20ByAsset_IdOrderByTsDesc(assetId)
                .stream()
                .sorted(Comparator.comparing(QuoteEntity::getTs))
                .map(quote -> new QuoteResponse(
                        quote.getId(),
                        quote.getAsset().getId(),
                        quote.getAsset().getTicker(),
                        quote.getAsset().getName(),
                        quote.getAsset().getCurrency(),
                        quote.getPrice(),
                        quote.getTs()
                ))
                .toList();
    }

    @GetMapping("/latest")
    public ResponseEntity<QuoteResponse> getLatestByTicker(@RequestParam String ticker) {
        return ResponseEntity.ok(quoteService.getLatestQuoteByTicker(ticker));
    }

    @PostMapping("/update-stock")
    public ResponseEntity<String> updateStockQuote(@RequestParam String ticker) {
        try {
            return ResponseEntity.ok(quoteUpdateService.updateStockQuote(ticker).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
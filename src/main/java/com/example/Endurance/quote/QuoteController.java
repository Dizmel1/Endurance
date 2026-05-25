package com.example.Endurance.quote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/latest/asset/{assetId}")
    public ResponseEntity<QuoteResponse> getLatestByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(quoteService.getLatestQuoteByAssetId(assetId));
    }

    @GetMapping("/latest")
    public ResponseEntity<QuoteResponse> getLatestByTicker(@RequestParam String ticker) {
        return ResponseEntity.ok(quoteService.getLatestQuoteByTicker(ticker));
    }
}
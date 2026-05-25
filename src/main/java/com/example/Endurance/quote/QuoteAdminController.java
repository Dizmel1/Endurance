package com.example.Endurance.quote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/quotes")
public class QuoteAdminController {

    private final QuoteUpdateService quoteUpdateService;

    public QuoteAdminController(QuoteUpdateService quoteUpdateService) {
        this.quoteUpdateService = quoteUpdateService;
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateQuote(
            @RequestParam String ticker,
            @RequestParam String from,
            @RequestParam String to
    ) {
        quoteUpdateService.updateCurrencyQuote(ticker, from, to);
        return ResponseEntity.ok().build();
    }

}

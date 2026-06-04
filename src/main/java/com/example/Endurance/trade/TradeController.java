package com.example.Endurance.trade;

import com.example.Endurance.transaction.TransactionEntity;
import com.example.Endurance.transaction.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/buy")
    public ResponseEntity<TradeResponse> buy(@RequestBody BuyRequest request) {
        return ResponseEntity.ok(tradeService.buy(request));
    }

    @PostMapping("/sell")
    public TransactionResponse sellAsset(@Valid @RequestBody SellAssetRequest request) {
        TransactionEntity transaction = tradeService.sellAsset(request);

        BigDecimal amount = transaction.getQty().multiply(transaction.getPrice());

        return new TransactionResponse(
                transaction.getId(),
                transaction.getPortfolio().getId(),
                transaction.getAsset().getId(),
                transaction.getAsset().getTicker(),
                transaction.getAsset().getName(),
                transaction.getType(),
                transaction.getQty(),
                transaction.getPrice(),
                amount,
                transaction.getFee(),
                transaction.getTs()
        );
    }
}
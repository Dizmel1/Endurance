package com.example.Endurance.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeResponse(
        Long transactionId,
        Long portfolioId,
        Long assetId,
        String ticker,
        String type,
        BigDecimal qty,
        BigDecimal price,
        BigDecimal total,
        BigDecimal fee,
        LocalDateTime ts
) {
}
package com.example.Endurance.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long portfolioId,
        Long assetId,
        String ticker,
        String assetName,
        String type,
        BigDecimal qty,
        BigDecimal price,
        BigDecimal amount,
        BigDecimal fee,
        LocalDateTime ts
) {
}
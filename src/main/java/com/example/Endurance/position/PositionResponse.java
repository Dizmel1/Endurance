package com.example.Endurance.position;

import java.math.BigDecimal;

public record PositionResponse(
        Long id,
        Long portfolioId,
        Long assetId,
        String ticker,
        String assetName,
        BigDecimal qty,
        BigDecimal avgPrice,
        BigDecimal currentPrice,
        BigDecimal currentValue,
        BigDecimal profit
) {
}
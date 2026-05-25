package com.example.Endurance.quote;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuoteResponse(
        Long id,
        Long assetId,
        String ticker,
        BigDecimal price,
        LocalDateTime ts
) {
}

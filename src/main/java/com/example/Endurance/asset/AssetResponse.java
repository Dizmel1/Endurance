package com.example.Endurance.asset;

import java.math.BigDecimal;

public record AssetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String ticker,
        String name,
        BigDecimal startBalance,
        String currency,
        Boolean active
) {
}
package com.example.Endurance.trade;

import java.math.BigDecimal;

public record BuyRequest(
        Long assetId,
        BigDecimal qty
) {
}

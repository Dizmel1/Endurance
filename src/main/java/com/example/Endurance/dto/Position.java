package com.example.Endurance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;

public record Position(
        @Null
        Long id,
        @Null
        Long portfolioId,
        @Null
        Long assetId,
        @NotNull
        BigDecimal qty,
        @NotNull
        BigDecimal avgPrice
) {

}

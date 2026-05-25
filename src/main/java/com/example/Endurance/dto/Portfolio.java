package com.example.Endurance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;
import java.time.Instant;

public record Portfolio(
        @Null
        Long id,
        @Null
        Long userId,
        @NotNull
        String name,
        @NotNull
        String currency,
        @NotNull
        BigDecimal startBalance,
        @NotNull
        BigDecimal cashBalance,
        @NotNull
        Instant createdAt
) {
}

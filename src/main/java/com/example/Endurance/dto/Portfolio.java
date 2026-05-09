package com.example.Endurance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

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
        Double startBalance,
        @NotNull
        Double cashBalance,
        @NotNull
        Instant createdAt
) {
}

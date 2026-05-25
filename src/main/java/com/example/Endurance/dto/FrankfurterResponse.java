package com.example.Endurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FrankfurterResponse(
        LocalDate date,
        String base,
        String quote,
        BigDecimal rate
) {
}

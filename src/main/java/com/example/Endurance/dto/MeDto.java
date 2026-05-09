package com.example.Endurance.dto;

import java.util.List;

public record MeDto(
        String username,
        List<String> roles
) {
}

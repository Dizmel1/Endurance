package com.example.Endurance.dto;

import java.util.List;

public record MeDto(
        Long id,
        String username,
        String email,
        List<String> roles
) {
}

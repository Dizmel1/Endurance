package com.example.Endurance.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Size(min = 5, max = 20)
        String username,
        @NotBlank
        @Size(min = 5, max = 20)
        String password
) {}

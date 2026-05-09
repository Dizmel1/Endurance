package com.example.Endurance.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 2, max = 50)
        String email,
        @NotBlank
        @Size(min = 3, max = 32)
        String username,
        @NotBlank
        @Size(min = 5, max = 72)
        String password
) {}

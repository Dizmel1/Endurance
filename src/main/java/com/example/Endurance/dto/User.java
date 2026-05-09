package com.example.Endurance.dto;

import com.example.Endurance.user.Roles;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record User(
        @Null
        Long id,
        @NotNull
        String name,
        @NotNull
        String email,
        @NotNull
        String password,
        @NotNull
        Roles roles
) {
}

package com.example.Endurance;

import com.example.Endurance.dto.Portfolio;
import com.example.Endurance.dto.User;

public record RegisterResponse(
        User user,
        Portfolio portfolio
) {
}

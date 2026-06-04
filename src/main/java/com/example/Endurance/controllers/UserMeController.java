package com.example.Endurance.controllers;

import com.example.Endurance.dto.MeDto;
import com.example.Endurance.user.UserEntity;
import com.example.Endurance.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserMeController {

    private final UserRepository userRepository;

    public UserMeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public MeDto me(Authentication authentication) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new MeDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles
        );
    }
}
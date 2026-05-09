package com.example.Endurance.controllers;

import com.example.Endurance.dto.MeDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserMeController {
    @GetMapping("/me")
    public MeDto me(@AuthenticationPrincipal User user) {
        return new MeDto(
                user.getUsername(),
                user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
        );
    }
}

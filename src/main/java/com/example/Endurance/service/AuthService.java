package com.example.Endurance.service;

import com.example.Endurance.RegisterResponse;
import com.example.Endurance.dto.Portfolio;
import com.example.Endurance.dto.User;
import com.example.Endurance.portfolio.PortfolioEntity;
import com.example.Endurance.portfolio.PortfolioRepository;
import com.example.Endurance.request.LoginRequest;
import com.example.Endurance.request.RegisterRequest;
import com.example.Endurance.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsUserEntiesByName(req.username())) {
            throw new RuntimeException("Username is already in use " + req.username() + " already exists");
        }
        String hash = passwordEncoder.encode(req.password());
        UserEntity entity = new UserEntity();
        entity.setEmail(req.email());
        entity.setPassword(hash);
        entity.setName(req.username());
        entity.setRoles(Roles.USER);

        PortfolioEntity portfolioEntity = new PortfolioEntity();
        portfolioEntity.setUser(entity);
        portfolioEntity.setName("Основной портфель");
        portfolioEntity.setCurrency("USD");
        portfolioEntity.setStartBalance(10000.00);
        portfolioEntity.setCashBalance(10000.00);
        portfolioEntity.setCreatedAt(Instant.now());

        UserEntity saved = userRepository.save(entity);
        PortfolioEntity portfolioSaved = portfolioRepository.save(portfolioEntity);

        User user = new User(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPassword(),
                saved.getRoles()
        );

        Portfolio portfolio = new Portfolio(
                portfolioSaved.getId(),
                portfolioSaved.getUser().getId(),
                portfolioSaved.getName(),
                portfolioSaved.getCurrency(),
                portfolioSaved.getStartBalance(),
                portfolioSaved.getCashBalance(),
                portfolioSaved.getCreatedAt()
        );
        return new RegisterResponse(user, portfolio);
    }

    @Transactional
    public User login(LoginRequest req) {
        UserEntity saved = userRepository.findByName(req.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDomain(saved);
    }

}

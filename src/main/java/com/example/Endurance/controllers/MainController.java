package com.example.Endurance.controllers;

import com.example.Endurance.RegisterResponse;
import com.example.Endurance.SignResponse;
import com.example.Endurance.dto.Portfolio;
import com.example.Endurance.portfolio.PortfolioEntity;
import com.example.Endurance.portfolio.PortfolioRepository;
import com.example.Endurance.position.PositionResponse;
import com.example.Endurance.position.PositionService;
import com.example.Endurance.service.AuthService;
import com.example.Endurance.request.LoginRequest;
import com.example.Endurance.request.RegisterRequest;
import com.example.Endurance.service.CurrentUserService;
import com.example.Endurance.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class MainController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final CurrentUserService currentUserService;
    private final PortfolioRepository portfolioRepository;
    private final PositionService positionService;

    public MainController(AuthService authService, AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository, CurrentUserService currentUserService, PortfolioRepository portfolioRepository, PositionService positionService) {
        this.authService = authService;
        this.securityContextRepository = securityContextRepository;
        this.authenticationManager = authenticationManager;
        this.currentUserService = currentUserService;
        this.portfolioRepository = portfolioRepository;
        this.positionService = positionService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<RegisterResponse> registration(@Valid @RequestBody RegisterRequest req){
        RegisterResponse created = authService.register(req);
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignResponse> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response
            ){
        User user = authService.login(req);
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        request.getSession(true);
        request.changeSessionId();
        securityContextRepository.saveContext(context, request, response);
        return ResponseEntity.status(200).body(new SignResponse(user.name()));
    }

    @GetMapping("/portfolio")
    public Portfolio portfolio() {
        Long userId = currentUserService.getUserId();

        PortfolioEntity entity = portfolioRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Портфель пользователя не найден"));

        return new Portfolio(
                entity.getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getCurrency(),
                entity.getStartBalance(),
                entity.getCashBalance(),
                entity.getCreatedAt()
        );
    }

    @GetMapping("/positions")
    public List<PositionResponse> positions() {
        return positionService.getCurrentUserPositions();
    }
}

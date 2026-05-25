package com.example.Endurance.transaction;

import com.example.Endurance.portfolio.PortfolioEntity;
import com.example.Endurance.portfolio.PortfolioRepository;
import com.example.Endurance.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final CurrentUserService currentUserService;

    public TransactionService(
            TransactionRepository transactionRepository,
            PortfolioRepository portfolioRepository,
            CurrentUserService currentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.portfolioRepository = portfolioRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getCurrentUserTransactions() {
        Long userId = currentUserService.getUserId();

        PortfolioEntity portfolio = portfolioRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Портфель пользователя не найден"));

        return transactionRepository.findAllByPortfolio_IdOrderByTsDesc(portfolio.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(TransactionEntity transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getPortfolio().getId(),
                transaction.getAsset().getId(),
                transaction.getAsset().getTicker(),
                transaction.getType(),
                transaction.getQty(),
                transaction.getPrice(),
                transaction.getFee(),
                transaction.getTs()
        );
    }
}
package com.example.Endurance.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByPortfolio_IdOrderByTsDesc(Long portfolioId);
}
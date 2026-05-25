package com.example.Endurance.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository  extends JpaRepository<PortfolioEntity, Long> {
    Optional<PortfolioEntity> findByUser_Id(Long userId);

    Optional<PortfolioEntity> findByUserId(Long id);
}

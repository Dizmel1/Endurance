package com.example.Endurance.position;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    Optional<PositionEntity> findByPortfolio_IdAndAsset_Id(Long portfolioId, Long assetId);
    List<PositionEntity> findAllByPortfolioId(Long id);
}
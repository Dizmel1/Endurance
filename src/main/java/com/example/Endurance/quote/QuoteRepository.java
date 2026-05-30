package com.example.Endurance.quote;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuoteRepository extends JpaRepository<QuoteEntity, Long> {
    Optional<QuoteEntity> findTopByAsset_IdOrderByTsDesc(Long assetId);
    Optional<QuoteEntity> findTopByAsset_TickerOrderByTsDesc(String ticker);
    Optional<QuoteEntity> findTopByAssetIdOrderByTsDesc(Long id) ;
}
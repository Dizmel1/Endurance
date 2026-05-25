package com.example.Endurance.asset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
    Optional<AssetEntity> findByTicker(String ticker);
    List<AssetEntity> findAllByActiveTrue();
}
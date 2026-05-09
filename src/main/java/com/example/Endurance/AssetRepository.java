package com.example.Endurance;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Endurance.asset.AssetEntity;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
    Optional<AssetEntity> findByTicker(String ticker);
}
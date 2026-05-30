package com.example.Endurance.asset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategoryEntity, Long> {

    Optional<AssetCategoryEntity> findByName(String name);
}
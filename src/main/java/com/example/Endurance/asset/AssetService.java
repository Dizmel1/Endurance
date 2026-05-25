package com.example.Endurance.asset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> getAllActiveAssets() {
        return assetRepository.findAllByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AssetResponse toResponse(AssetEntity asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getCategory().getId(),
                asset.getCategory().getName(),
                asset.getTicker(),
                asset.getName(),
                asset.getStartBalance(),
                asset.getCurrency(),
                asset.getActive()
        );
    }
}
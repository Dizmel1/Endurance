package com.example.Endurance.position;

import com.example.Endurance.asset.AssetEntity;
import com.example.Endurance.portfolio.PortfolioEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "positions")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity asset;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal qty;

    @Column(name = "avg_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal avgPrice;

    public PositionEntity() {
    }

}
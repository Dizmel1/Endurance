package com.example.Endurance;

import com.example.Endurance.asset.AssetEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "quotes")
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // В БД это asset_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity asset;

    @Column(nullable = false)
    private LocalDateTime ts;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal price;

    public QuoteEntity() {
    }

    public QuoteEntity(Long id, AssetEntity asset, LocalDateTime ts, BigDecimal price) {
        this.id = id;
        this.asset = asset;
        this.ts = ts;
        this.price = price;
    }

}

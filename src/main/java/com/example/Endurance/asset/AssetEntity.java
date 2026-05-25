package com.example.Endurance.asset;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "assets")
public class AssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategoryEntity category;

    @Column(nullable = false, unique = true, length = 20)
    private String ticker;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "start_balance", nullable = false, precision = 19, scale = 6)
    private BigDecimal startBalance;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    public AssetEntity() {
    }

    public AssetEntity(
            Long id,
            AssetCategoryEntity category,
            String ticker,
            String name,
            BigDecimal startBalance,
            String currency,
            Boolean active
    ) {
        this.id = id;
        this.category = category;
        this.ticker = ticker;
        this.name = name;
        this.startBalance = startBalance;
        this.currency = currency;
        this.active = active;
    }

}
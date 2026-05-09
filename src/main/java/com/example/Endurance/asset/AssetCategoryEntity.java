package com.example.Endurance.asset;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "asset_categories")
public class AssetCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public AssetCategoryEntity() {
    }

    public AssetCategoryEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

package com.example.Endurance.trade;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SellAssetRequest {

    @NotNull
    private Long assetId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal qty;

}
package com.example.Endurance.portfolio;

import com.example.Endurance.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "portfolios")
public class PortfolioEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Column(name = "name")
    private String name;
    @Column(name = "currency")
    private String currency;
    @Column(name = "start_balance")
    private BigDecimal startBalance;
    @Column(name = "cash_balance")
    private BigDecimal cashBalance;
    @Column(name = "created_at")
    private Instant createdAt;

    public PortfolioEntity() {}

    public PortfolioEntity(Long id, UserEntity user, String name, String currency, BigDecimal startBalance, BigDecimal cashBalance, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.currency = currency;
        this.startBalance = startBalance;
        this.cashBalance = cashBalance;
        this.createdAt = createdAt;
    }
}

package com.example.Endurance.portfolio;

import com.example.Endurance.dto.Portfolio;
import com.example.Endurance.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {
    public Portfolio toDomain(PortfolioEntity portfolio){
        return new Portfolio(
                portfolio.getId(),
                portfolio.getUser().getId(),
                portfolio.getName(),
                portfolio.getCurrency(),
                portfolio.getStartBalance(),
                portfolio.getCashBalance(),
                portfolio.getCreatedAt()
        );
    }
    public PortfolioEntity toEntity(Portfolio portfolio, UserEntity userEntity){
        return new PortfolioEntity(
                portfolio.id(),
                userEntity,
                portfolio.name(),
                portfolio.currency(),
                portfolio.startBalance(),
                portfolio.cashBalance(),
                portfolio.createdAt()
        );
    }
}

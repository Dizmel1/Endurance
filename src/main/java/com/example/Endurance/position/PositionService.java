package com.example.Endurance.position;

import com.example.Endurance.asset.AssetEntity;
import com.example.Endurance.portfolio.PortfolioEntity;
import com.example.Endurance.portfolio.PortfolioRepository;
import com.example.Endurance.quote.QuoteEntity;
import com.example.Endurance.quote.QuoteRepository;
import com.example.Endurance.service.CurrentUserService;
import com.example.Endurance.user.UserEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PositionService {

    private final CurrentUserService currentUserService;
    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final QuoteRepository quoteRepository;

    public PositionService(CurrentUserService currentUserService,
                           PortfolioRepository portfolioRepository,
                           PositionRepository positionRepository,
                           QuoteRepository quoteRepository) {
        this.currentUserService = currentUserService;
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.quoteRepository = quoteRepository;
    }

    public List<PositionResponse> getCurrentUserPositions() {
        UserEntity currentUser = currentUserService.getCurrentUser();

        PortfolioEntity portfolio = portfolioRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Портфель текущего пользователя не найден"));

        List<PositionEntity> positions = positionRepository.findAllByPortfolioId(portfolio.getId());

        return positions.stream()
                .map(position -> buildPositionResponse(position, portfolio))
                .toList();
    }

    private PositionResponse buildPositionResponse(PositionEntity position, PortfolioEntity portfolio) {
        AssetEntity asset = position.getAsset();

        BigDecimal qty = position.getQty();
        BigDecimal avgPrice = position.getAvgPrice();

        QuoteEntity latestQuote = quoteRepository
                .findTopByAssetIdOrderByTsDesc(asset.getId())
                .orElse(null);

        BigDecimal currentPrice = latestQuote != null
                ? latestQuote.getPrice()
                : avgPrice;

        BigDecimal currentValue = qty.multiply(currentPrice);

        BigDecimal profit = currentPrice
                .subtract(avgPrice)
                .multiply(qty);

        return new PositionResponse(
                position.getId(),
                portfolio.getId(),
                asset.getId(),
                asset.getTicker(),
                asset.getName(),
                qty,
                avgPrice,
                currentPrice,
                currentValue,
                profit
        );
    }
}
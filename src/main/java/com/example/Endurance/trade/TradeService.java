package com.example.Endurance.trade;

import com.example.Endurance.asset.AssetEntity;
import com.example.Endurance.asset.AssetRepository;
import com.example.Endurance.portfolio.PortfolioEntity;
import com.example.Endurance.portfolio.PortfolioRepository;
import com.example.Endurance.position.PositionEntity;
import com.example.Endurance.position.PositionRepository;
import com.example.Endurance.quote.QuoteEntity;
import com.example.Endurance.quote.QuoteRepository;
import com.example.Endurance.transaction.TransactionEntity;
import com.example.Endurance.transaction.TransactionRepository;
import com.example.Endurance.user.UserEntity;
import com.example.Endurance.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TradeService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final QuoteRepository quoteRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;

    public TradeService(
            UserRepository userRepository,
            PortfolioRepository portfolioRepository,
            AssetRepository assetRepository,
            QuoteRepository quoteRepository,
            PositionRepository positionRepository,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.quoteRepository = quoteRepository;
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TradeResponse buy(BuyRequest request) {
        if (request.qty() == null || request.qty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Количество должно быть больше нуля");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        PortfolioEntity portfolio = portfolioRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Портфель пользователя не найден"));

        AssetEntity asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new RuntimeException("Актив не найден"));

        QuoteEntity quote = quoteRepository.findTopByAsset_IdOrderByTsDesc(asset.getId())
                .orElseThrow(() -> new RuntimeException("Курс для актива не найден"));

        BigDecimal qty = request.qty();
        BigDecimal price = quote.getPrice();
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal total = qty.multiply(price).add(fee);

        if (portfolio.getCashBalance().compareTo(total) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        portfolio.setCashBalance(portfolio.getCashBalance().subtract(total));
        portfolioRepository.save(portfolio);

        PositionEntity position = positionRepository
                .findByPortfolio_IdAndAsset_Id(portfolio.getId(), asset.getId())
                .orElse(null);

        if (position == null) {
            position = new PositionEntity();
            position.setPortfolio(portfolio);
            position.setAsset(asset);
            position.setQty(qty);
            position.setAvgPrice(price);
        } else {
            BigDecimal oldQty = position.getQty();
            BigDecimal oldAvgPrice = position.getAvgPrice();

            BigDecimal oldTotal = oldQty.multiply(oldAvgPrice);
            BigDecimal newTotal = qty.multiply(price);
            BigDecimal finalQty = oldQty.add(qty);

            BigDecimal newAvgPrice = oldTotal.add(newTotal).divide(finalQty, 6, java.math.RoundingMode.HALF_UP);

            position.setQty(finalQty);
            position.setAvgPrice(newAvgPrice);
        }

        positionRepository.save(position);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setPortfolio(portfolio);
        transaction.setAsset(asset);
        transaction.setType("BUY");
        transaction.setQty(qty);
        transaction.setPrice(price);
        transaction.setFee(fee);
        transaction.setTs(LocalDateTime.now());

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        return new TradeResponse(
                savedTransaction.getId(),
                portfolio.getId(),
                asset.getId(),
                asset.getTicker(),
                savedTransaction.getType(),
                qty,
                price,
                total,
                fee,
                savedTransaction.getTs()
        );
    }
}
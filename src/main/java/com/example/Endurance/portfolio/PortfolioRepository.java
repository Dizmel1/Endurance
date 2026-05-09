package com.example.Endurance.portfolio;

import com.example.Endurance.dto.Portfolio;
import com.example.Endurance.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository  extends JpaRepository<PortfolioEntity, Long> {
    Portfolio findByUser_Id(Long userId);
}

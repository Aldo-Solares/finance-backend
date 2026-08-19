package com.finance.backend.modules.trading.tradingmovement.repository;

import com.finance.backend.modules.trading.tradingmovement.model.TradingMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradingMovementRepository
        extends JpaRepository<TradingMovement, Long> {

    List<TradingMovement> findAllByOrderByDateAscTradingMovementIdAsc();

    List<TradingMovement> findByTradingAccountTradingAccountIdOrderByDateAscTradingMovementIdAsc(
            Long tradingAccountId);
}
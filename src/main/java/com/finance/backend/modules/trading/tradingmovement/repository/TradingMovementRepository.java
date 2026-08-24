package com.finance.backend.modules.trading.tradingmovement.repository;

import com.finance.backend.modules.trading.tradingmovement.model.TradingMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradingMovementRepository
                extends JpaRepository<TradingMovement, Long> {

        List<TradingMovement> findByTradingAccountUserEmailIgnoreCaseOrderByDateAscTradingMovementIdAsc(
                        String email);

        List<TradingMovement> findByTradingAccountTradingAccountIdAndTradingAccountUserEmailIgnoreCaseOrderByDateAscTradingMovementIdAsc(
                        Long tradingAccountId,
                        String email);

        Optional<TradingMovement> findByTradingMovementIdAndTradingAccountUserEmailIgnoreCase(
                        Long tradingMovementId,
                        String email);
}
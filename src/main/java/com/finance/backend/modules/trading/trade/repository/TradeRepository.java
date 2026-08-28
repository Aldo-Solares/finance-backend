package com.finance.backend.modules.trading.trade.repository;

import com.finance.backend.modules.trading.trade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradeRepository
                extends JpaRepository<Trade, Long> {

        List<Trade> findByUserTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                        String email);

        List<Trade> findByUserTradingAccountUserTradingAccountIdAndUserTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                        Long userTradingAccountId,
                        String email);

        Optional<Trade> findByTradeIdAndUserTradingAccountUserEmailIgnoreCase(
                        Long tradeId,
                        String email);
}
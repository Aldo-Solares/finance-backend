package com.finance.backend.modules.trading.trade.repository;

import com.finance.backend.modules.trading.trade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradeRepository
                extends JpaRepository<Trade, Long> {

        List<Trade> findByTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                        String email);

        List<Trade> findByTradingAccountTradingAccountIdAndTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                        Long tradingAccountId,
                        String email);

        Optional<Trade> findByTradeIdAndTradingAccountUserEmailIgnoreCase(
                        Long tradeId,
                        String email);
}
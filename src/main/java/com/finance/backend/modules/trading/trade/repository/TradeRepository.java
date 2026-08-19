package com.finance.backend.modules.trading.trade.repository;

import com.finance.backend.modules.trading.trade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository
        extends JpaRepository<Trade, Long> {

    List<Trade> findAllByOrderByDateAscTradeIdAsc();

    List<Trade> findByTradingAccountTradingAccountIdOrderByDateAscTradeIdAsc(
            Long tradingAccountId);

    List<Trade> findByTradingAccountTradingAccountIdAndInstrumentInstrumentIdOrderByDateAscTradeIdAsc(
            Long tradingAccountId,
            Long instrumentId);
}
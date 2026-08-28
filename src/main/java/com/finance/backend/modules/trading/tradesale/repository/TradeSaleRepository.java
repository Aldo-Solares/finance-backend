package com.finance.backend.modules.trading.tradesale.repository;

import com.finance.backend.modules.trading.tradesale.model.TradeSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeSaleRepository
                extends JpaRepository<TradeSale, Long> {

        List<TradeSale> findByTradeTradeIdOrderBySaleDateAscTradeSaleIdAsc(
                        Long tradeId);
}
package com.finance.backend.modules.trading.trade.dto;

import com.finance.backend.modules.trading.trade.model.TradeSide;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeResponse(
        Long tradeId,
        Long tradingAccountId,
        Long instrumentId,
        TradeSide side,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal commission,
        LocalDate date) {
}
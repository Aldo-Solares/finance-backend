package com.finance.backend.modules.trading.tradingmovement.dto;

import com.finance.backend.modules.trading.tradingmovement.model.TradingMovementType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradingMovementResponse(
        Long tradingMovementId,
        Long tradingAccountId,
        TradingMovementType type,
        BigDecimal amount,
        LocalDate date,
        String notes) {
}
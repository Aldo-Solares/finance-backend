package com.finance.backend.modules.trading.tradingmovement.dto;

import com.finance.backend.modules.trading.tradingmovement.model.TradingMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTradingMovementRequest(
        @NotNull Long tradingAccountId,
        @NotNull TradingMovementType type,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        LocalDate date,
        String notes) {
}
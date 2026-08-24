package com.finance.backend.modules.trading.tradingmovement.dto;

import com.finance.backend.modules.trading.tradingmovement.model.TradingMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTradingMovementRequest(

        @NotNull Long tradingAccountId,

        @NotNull TradingMovementType type,

        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,

        @NotNull LocalDate date,

        @Size(max = 1000) String notes

) {
}
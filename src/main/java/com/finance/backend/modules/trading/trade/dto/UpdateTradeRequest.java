package com.finance.backend.modules.trading.trade.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTradeRequest(

        @NotNull Long userTradingAccountId,

        @NotNull Long instrumentId,

        @NotNull BigDecimal quantity,

        @NotNull BigDecimal purchasePrice,

        @NotNull BigDecimal purchaseCommission,

        @NotNull BigDecimal purchaseCommissionRate,

        @NotNull LocalDate purchaseDate

) {
}
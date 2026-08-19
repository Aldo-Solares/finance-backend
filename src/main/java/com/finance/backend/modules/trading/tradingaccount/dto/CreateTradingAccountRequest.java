package com.finance.backend.modules.trading.tradingaccount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTradingAccountRequest(
        @NotNull Long userId,
        @NotBlank String name,
        @NotBlank String currency) {
}
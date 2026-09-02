package com.finance.backend.modules.trading.tradingaccount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTradingAccountRequest(
        @NotBlank String institution,

        @NotBlank String name,

        @NotNull Long currencyId,

        @NotNull Boolean active) {
}
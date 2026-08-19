package com.finance.backend.modules.trading.tradingaccount.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTradingAccountRequest(
        @NotBlank String name,
        @NotBlank String currency) {
}
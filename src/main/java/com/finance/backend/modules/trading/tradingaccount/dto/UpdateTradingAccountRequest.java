package com.finance.backend.modules.trading.tradingaccount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTradingAccountRequest(

        @NotBlank String institution,

        @NotBlank String name,

        @NotBlank String accountType,

        @NotBlank String currency,

        @NotNull Boolean active

) {
}
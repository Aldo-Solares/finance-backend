package com.finance.backend.modules.trading.usertradingaccount.dto;

import jakarta.validation.constraints.NotNull;

public record CreateUserTradingAccountRequest(
        @NotNull Long tradingAccountId) {
}
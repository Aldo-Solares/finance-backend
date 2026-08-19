package com.finance.backend.modules.trading.tradingaccount.dto;

import java.math.BigDecimal;

public record TradingAccountResponse(
        Long tradingAccountId,
        Long userId,
        String name,
        String currency,
        BigDecimal balance,
        BigDecimal investedAmount,
        BigDecimal availableAmount) {
}
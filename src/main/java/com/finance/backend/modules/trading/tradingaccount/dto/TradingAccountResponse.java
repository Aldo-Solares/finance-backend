package com.finance.backend.modules.trading.tradingaccount.dto;

public record TradingAccountResponse(
        Long tradingAccountId,
        String institution,
        String name,
        Long currencyId,
        String currencyCode,
        String currencySymbol,
        Boolean active) {
}
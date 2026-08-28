package com.finance.backend.modules.trading.tradingaccount.dto;

public record TradingAccountResponse(

        Long tradingAccountId,
        String institution,
        String name,
        String accountType,
        String currency,
        Boolean active

) {
}
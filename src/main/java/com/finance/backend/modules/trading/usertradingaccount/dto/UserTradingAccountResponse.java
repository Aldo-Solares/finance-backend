package com.finance.backend.modules.trading.usertradingaccount.dto;

public record UserTradingAccountResponse(
                Long userTradingAccountId,
                Long tradingAccountId,
                String institution,
                String name,
                Long currencyId,
                String currencyCode) {
}
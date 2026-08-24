package com.finance.backend.modules.trading.tradingaccount.dto;

public record TradingAccountResponse(

                Long tradingAccountId,

                Long userId,

                String name,

                String currency

) {
}
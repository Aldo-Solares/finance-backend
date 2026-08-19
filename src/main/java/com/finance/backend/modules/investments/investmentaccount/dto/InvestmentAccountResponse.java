package com.finance.backend.modules.investments.investmentaccount.dto;

import java.math.BigDecimal;

public record InvestmentAccountResponse(
        Long investmentAccountId,
        Long userId,
        String name,
        String currency,
        BigDecimal balance) {
}
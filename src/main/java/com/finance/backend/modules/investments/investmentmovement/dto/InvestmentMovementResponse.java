package com.finance.backend.modules.investments.investmentmovement.dto;

import com.finance.backend.modules.investments.investmentmovement.model.InvestmentMovementType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentMovementResponse(
        Long investmentMovementId,
        Long investmentAccountId,
        InvestmentMovementType type,
        BigDecimal amount,
        LocalDate date,
        String notes) {
}
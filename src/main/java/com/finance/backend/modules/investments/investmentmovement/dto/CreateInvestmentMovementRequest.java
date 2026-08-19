package com.finance.backend.modules.investments.investmentmovement.dto;

import com.finance.backend.modules.investments.investmentmovement.model.InvestmentMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInvestmentMovementRequest(
        @NotNull Long investmentAccountId,
        @NotNull InvestmentMovementType type,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        LocalDate date,
        String notes) {
}
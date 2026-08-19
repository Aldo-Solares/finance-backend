package com.finance.backend.modules.investments.investmentaccount.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInvestmentAccountRequest(
        @NotNull Long userId,
        @NotBlank String name,
        @NotBlank String currency) {
}
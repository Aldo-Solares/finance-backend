package com.finance.backend.modules.investments.investmentaccount.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateInvestmentAccountRequest(
        @NotBlank String name,
        @NotBlank String currency) {
}
package com.finance.backend.modules.catalogs.currency.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCurrencyRequest(
                @NotBlank String code,
                @NotBlank String symbol) {
}
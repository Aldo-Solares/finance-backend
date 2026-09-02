package com.finance.backend.modules.trading.instrument.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInstrumentRequest(
                @NotBlank String symbol,
                @NotBlank String name,
                @NotNull Long currencyId) {
}
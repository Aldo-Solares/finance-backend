package com.finance.backend.modules.trading.instrument.dto;

import com.finance.backend.modules.trading.instrument.model.InstrumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateInstrumentRequest(
        @NotBlank String symbol,
        @NotBlank String name,
        @NotNull InstrumentType type,
        @NotBlank String currency) {
}
package com.finance.backend.modules.trading.instrument.dto;

import com.finance.backend.modules.trading.instrument.model.InstrumentType;

public record InstrumentResponse(
        Long instrumentId,
        String symbol,
        String name,
        InstrumentType type,
        String currency) {
}
package com.finance.backend.modules.trading.instrument.dto;

public record InstrumentResponse(
                Long instrumentId,
                String symbol,
                String name,
                Long currencyId,
                String currencyCode) {
}
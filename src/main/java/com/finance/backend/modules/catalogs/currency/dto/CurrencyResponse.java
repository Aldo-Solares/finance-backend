package com.finance.backend.modules.catalogs.currency.dto;

public record CurrencyResponse(
                Long currencyId,
                String code,
                String symbol) {
}
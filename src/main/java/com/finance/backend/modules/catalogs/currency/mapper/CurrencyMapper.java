package com.finance.backend.modules.catalogs.currency.mapper;

import com.finance.backend.modules.catalogs.currency.dto.CreateCurrencyRequest;
import com.finance.backend.modules.catalogs.currency.dto.CurrencyResponse;
import com.finance.backend.modules.catalogs.currency.dto.UpdateCurrencyRequest;
import com.finance.backend.modules.catalogs.currency.model.Currency;

public final class CurrencyMapper {

    private CurrencyMapper() {
    }

    // ===================
    // CREATE REQUEST
    // ===================

    public static Currency toEntity(
            CreateCurrencyRequest request) {

        Currency currency = new Currency();

        currency.setCode(request.code());
        currency.setSymbol(request.symbol());

        return currency;
    }

    // ===================
    // UPDATE REQUEST
    // ===================

    public static void updateEntity(
            Currency currency,
            UpdateCurrencyRequest request) {

        currency.setCode(request.code());
        currency.setSymbol(request.symbol());
    }

    // ===================
    // RESPONSE
    // ===================

    public static CurrencyResponse toResponse(
            Currency currency) {

        return new CurrencyResponse(
                currency.getCurrencyId(),
                currency.getCode(),
                currency.getSymbol());
    }
}
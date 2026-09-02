package com.finance.backend.modules.catalogs.currency.repository;

import com.finance.backend.modules.catalogs.currency.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository
                extends JpaRepository<Currency, Long> {

        boolean existsByCode(String code);

        boolean existsByCodeAndCurrencyIdNot(
                        String code,
                        Long currencyId);

        Optional<Currency> findByCode(String code);
}
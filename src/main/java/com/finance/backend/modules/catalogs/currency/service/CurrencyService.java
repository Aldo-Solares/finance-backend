package com.finance.backend.modules.catalogs.currency.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.catalogs.currency.dto.CreateCurrencyRequest;
import com.finance.backend.modules.catalogs.currency.dto.CurrencyResponse;
import com.finance.backend.modules.catalogs.currency.dto.UpdateCurrencyRequest;
import com.finance.backend.modules.catalogs.currency.mapper.CurrencyMapper;
import com.finance.backend.modules.catalogs.currency.model.Currency;
import com.finance.backend.modules.catalogs.currency.repository.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CurrencyService {

        private final CurrencyRepository currencyRepository;

        public CurrencyService(
                        CurrencyRepository currencyRepository) {

                this.currencyRepository = currencyRepository;
        }

        // ===================
        // GET ALL
        // ===================

        @Transactional(readOnly = true)
        public List<CurrencyResponse> getCurrencies() {

                return currencyRepository.findAll()
                                .stream()
                                .map(CurrencyMapper::toResponse)
                                .toList();
        }

        // ===================
        // GET BY ID
        // ===================

        @Transactional(readOnly = true)
        public CurrencyResponse getCurrencyById(
                        Long currencyId) {

                Currency currency = findCurrency(currencyId);

                return CurrencyMapper.toResponse(currency);
        }

        // ===================
        // CREATE
        // ===================

        @Transactional
        public CurrencyResponse createCurrency(
                        CreateCurrencyRequest request) {

                String code = request.code().trim();

                if (currencyRepository.existsByCode(code)) {
                        throw new ConflictException(
                                        "Ya existe una moneda con el código indicado");
                }

                Currency currency = CurrencyMapper.toEntity(request);

                currency.setCode(code);
                currency.setSymbol(request.symbol().trim());

                Currency savedCurrency = currencyRepository.save(currency);

                return CurrencyMapper.toResponse(savedCurrency);
        }

        // ===================
        // UPDATE
        // ===================

        @Transactional
        public CurrencyResponse updateCurrency(
                        Long currencyId,
                        UpdateCurrencyRequest request) {

                Currency currency = findCurrency(currencyId);

                String code = request.code().trim();

                if (currencyRepository.existsByCodeAndCurrencyIdNot(
                                code,
                                currencyId)) {

                        throw new ConflictException(
                                        "Ya existe una moneda con el código indicado");
                }

                CurrencyMapper.updateEntity(currency, request);

                currency.setCode(code);
                currency.setSymbol(request.symbol().trim());

                Currency updatedCurrency = currencyRepository.save(currency);

                return CurrencyMapper.toResponse(updatedCurrency);
        }

        // ===================
        // DELETE
        // ===================

        @Transactional
        public void deleteCurrency(
                        Long currencyId) {

                Currency currency = findCurrency(currencyId);

                currencyRepository.delete(currency);
        }

        // ===================
        // FIND
        // ===================

        private Currency findCurrency(
                        Long currencyId) {

                return currencyRepository.findById(currencyId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Moneda no encontrada"));
        }
}
// modules/trading/tradingaccount/service/TradingAccountService.java

package com.finance.backend.modules.trading.tradingaccount.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.catalogs.currency.model.Currency;
import com.finance.backend.modules.catalogs.currency.repository.CurrencyRepository;
import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.mapper.TradingAccountMapper;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.repository.TradingAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TradingAccountService {

        private final TradingAccountRepository tradingAccountRepository;
        private final CurrencyRepository currencyRepository;

        public TradingAccountService(
                        TradingAccountRepository tradingAccountRepository,
                        CurrencyRepository currencyRepository) {

                this.tradingAccountRepository = tradingAccountRepository;
                this.currencyRepository = currencyRepository;
        }

        // ===================
        // QUERIES
        // ===================

        @Transactional(readOnly = true)
        public List<TradingAccountResponse> findAll() {

                return tradingAccountRepository
                                .findAllByOrderByTradingAccountIdAsc()
                                .stream()
                                .map(TradingAccountMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TradingAccountResponse findById(
                        Long tradingAccountId) {

                return TradingAccountMapper.toResponse(
                                getEntity(tradingAccountId));
        }

        // ===================
        // CREATE
        // ===================

        public TradingAccountResponse create(
                        CreateTradingAccountRequest request) {

                Currency currency = getCurrency(
                                request.currencyId());

                TradingAccount tradingAccount = TradingAccountMapper.toEntity(
                                request,
                                currency);

                TradingAccount savedTradingAccount = tradingAccountRepository.save(
                                tradingAccount);

                return TradingAccountMapper.toResponse(
                                savedTradingAccount);
        }

        // ===================
        // UPDATE
        // ===================

        public TradingAccountResponse update(
                        Long tradingAccountId,
                        UpdateTradingAccountRequest request) {

                TradingAccount tradingAccount = getEntity(tradingAccountId);

                Currency currency = getCurrency(
                                request.currencyId());

                TradingAccountMapper.updateEntity(
                                tradingAccount,
                                request,
                                currency);

                TradingAccount savedTradingAccount = tradingAccountRepository.save(
                                tradingAccount);

                return TradingAccountMapper.toResponse(
                                savedTradingAccount);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long tradingAccountId) {

                TradingAccount tradingAccount = getEntity(tradingAccountId);

                tradingAccountRepository.delete(
                                tradingAccount);
        }

        // ===================
        // ENTITY
        // ===================

        @Transactional(readOnly = true)
        public TradingAccount getEntity(
                        Long tradingAccountId) {

                return tradingAccountRepository
                                .findById(tradingAccountId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Cuenta de trading no encontrada"));
        }

        // ===================
        // CURRENCY
        // ===================

        @Transactional(readOnly = true)
        private Currency getCurrency(
                        Long currencyId) {

                return currencyRepository
                                .findById(currencyId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Moneda no encontrada"));
        }
}
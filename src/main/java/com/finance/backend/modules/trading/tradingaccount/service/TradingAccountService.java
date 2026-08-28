package com.finance.backend.modules.trading.tradingaccount.service;

import com.finance.backend.exception.ResourceNotFoundException;
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

        public TradingAccountService(
                        TradingAccountRepository tradingAccountRepository) {

                this.tradingAccountRepository = tradingAccountRepository;
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

                TradingAccount tradingAccount = TradingAccountMapper.toEntity(request);

                TradingAccount savedTradingAccount = tradingAccountRepository.save(tradingAccount);

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

                TradingAccountMapper.updateEntity(
                                tradingAccount,
                                request);

                TradingAccount savedTradingAccount = tradingAccountRepository.save(tradingAccount);

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
}
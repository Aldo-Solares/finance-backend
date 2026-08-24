package com.finance.backend.modules.trading.tradingaccount.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.mapper.TradingAccountMapper;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.repository.TradingAccountRepository;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TradingAccountService {

        private final TradingAccountRepository tradingAccountRepository;
        private final UserRepository userRepository;

        public TradingAccountService(
                        TradingAccountRepository tradingAccountRepository,
                        UserRepository userRepository) {

                this.tradingAccountRepository = tradingAccountRepository;

                this.userRepository = userRepository;
        }

        // ===================
        // QUERIES
        // ===================

        @Transactional(readOnly = true)
        public List<TradingAccountResponse> findAll(
                        String email) {

                return tradingAccountRepository
                                .findByUserEmailIgnoreCaseOrderByTradingAccountIdAsc(
                                                email)
                                .stream()
                                .map(
                                                TradingAccountMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TradingAccountResponse findById(
                        Long tradingAccountId,
                        String email) {

                return TradingAccountMapper.toResponse(
                                getEntity(
                                                tradingAccountId,
                                                email));
        }

        // ===================
        // CREATE
        // ===================

        public TradingAccountResponse create(
                        CreateTradingAccountRequest request,
                        String email) {

                User user = userRepository
                                .findById(
                                                request.userId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));

                if (!user.getEmail()
                                .equalsIgnoreCase(email)) {

                        throw new ResourceNotFoundException(
                                        "Usuario no encontrado");
                }

                TradingAccount tradingAccount = TradingAccountMapper.toEntity(
                                request,
                                user);

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
                        UpdateTradingAccountRequest request,
                        String email) {

                TradingAccount tradingAccount = getEntity(
                                tradingAccountId,
                                email);

                TradingAccountMapper.updateEntity(
                                tradingAccount,
                                request);

                TradingAccount savedTradingAccount = tradingAccountRepository.save(
                                tradingAccount);

                return TradingAccountMapper.toResponse(
                                savedTradingAccount);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long tradingAccountId,
                        String email) {

                TradingAccount tradingAccount = getEntity(
                                tradingAccountId,
                                email);

                tradingAccountRepository.delete(
                                tradingAccount);
        }

        // ===================
        // ENTITY
        // ===================

        @Transactional(readOnly = true)
        public TradingAccount getEntity(
                        Long tradingAccountId,
                        String email) {

                return tradingAccountRepository
                                .findByTradingAccountIdAndUserEmailIgnoreCase(
                                                tradingAccountId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Cuenta de trading no encontrada"));
        }
}
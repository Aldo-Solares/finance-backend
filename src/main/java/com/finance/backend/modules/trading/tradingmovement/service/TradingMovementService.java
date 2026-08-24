package com.finance.backend.modules.trading.tradingmovement.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.service.TradingAccountService;
import com.finance.backend.modules.trading.tradingmovement.dto.CreateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.dto.TradingMovementResponse;
import com.finance.backend.modules.trading.tradingmovement.dto.UpdateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.mapper.TradingMovementMapper;
import com.finance.backend.modules.trading.tradingmovement.model.TradingMovement;
import com.finance.backend.modules.trading.tradingmovement.repository.TradingMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TradingMovementService {

        private final TradingMovementRepository tradingMovementRepository;
        private final TradingAccountService tradingAccountService;

        public TradingMovementService(
                        TradingMovementRepository tradingMovementRepository,
                        TradingAccountService tradingAccountService) {

                this.tradingMovementRepository = tradingMovementRepository;

                this.tradingAccountService = tradingAccountService;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<TradingMovementResponse> findAll(
                        String email) {

                return tradingMovementRepository
                                .findByTradingAccountUserEmailIgnoreCaseOrderByDateAscTradingMovementIdAsc(
                                                email)
                                .stream()
                                .map(
                                                TradingMovementMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public TradingMovementResponse findById(
                        Long tradingMovementId,
                        String email) {

                return TradingMovementMapper.toResponse(
                                getEntity(
                                                tradingMovementId,
                                                email));
        }

        // ===================
        // FIND BY ACCOUNT
        // ===================

        @Transactional(readOnly = true)
        public List<TradingMovementResponse> findByTradingAccountId(
                        Long tradingAccountId,
                        String email) {

                tradingAccountService.getEntity(
                                tradingAccountId,
                                email);

                return tradingMovementRepository
                                .findByTradingAccountTradingAccountIdAndTradingAccountUserEmailIgnoreCaseOrderByDateAscTradingMovementIdAsc(
                                                tradingAccountId,
                                                email)
                                .stream()
                                .map(
                                                TradingMovementMapper::toResponse)
                                .toList();
        }

        // ===================
        // CREATE
        // ===================

        public TradingMovementResponse create(
                        CreateTradingMovementRequest request,
                        String email) {

                TradingAccount tradingAccount = tradingAccountService.getEntity(
                                request.tradingAccountId(),
                                email);

                TradingMovement movement = TradingMovementMapper.toEntity(
                                request,
                                tradingAccount);

                TradingMovement savedMovement = tradingMovementRepository.save(
                                movement);

                return TradingMovementMapper.toResponse(
                                savedMovement);
        }

        // ===================
        // UPDATE
        // ===================

        public TradingMovementResponse update(
                        Long tradingMovementId,
                        UpdateTradingMovementRequest request,
                        String email) {

                TradingMovement movement = getEntity(
                                tradingMovementId,
                                email);

                TradingAccount tradingAccount = tradingAccountService.getEntity(
                                request.tradingAccountId(),
                                email);

                TradingMovementMapper.updateEntity(
                                movement,
                                request,
                                tradingAccount);

                TradingMovement savedMovement = tradingMovementRepository.save(
                                movement);

                return TradingMovementMapper.toResponse(
                                savedMovement);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long tradingMovementId,
                        String email) {

                TradingMovement movement = getEntity(
                                tradingMovementId,
                                email);

                tradingMovementRepository.delete(
                                movement);
        }

        // ===================
        // ENTITY
        // ===================

        @Transactional(readOnly = true)
        public TradingMovement getEntity(
                        Long tradingMovementId,
                        String email) {

                return tradingMovementRepository
                                .findByTradingMovementIdAndTradingAccountUserEmailIgnoreCase(
                                                tradingMovementId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Movimiento de trading no encontrado"));
        }
}
package com.finance.backend.modules.trading.tradingmovement.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.service.TradingAccountService;
import com.finance.backend.modules.trading.tradingmovement.dto.CreateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.dto.TradingMovementResponse;
import com.finance.backend.modules.trading.tradingmovement.mapper.TradingMovementMapper;
import com.finance.backend.modules.trading.tradingmovement.model.TradingMovement;
import com.finance.backend.modules.trading.tradingmovement.repository.TradingMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Transactional(readOnly = true)
    public List<TradingMovementResponse> findAll() {
        return tradingMovementRepository
                .findAllByOrderByDateAscTradingMovementIdAsc()
                .stream()
                .map(TradingMovementMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TradingMovementResponse findById(
            Long tradingMovementId) {

        return TradingMovementMapper.toResponse(
                getMovement(tradingMovementId));
    }

    @Transactional(readOnly = true)
    public List<TradingMovementResponse> findByTradingAccountId(
            Long tradingAccountId) {

        tradingAccountService.getEntity(
                tradingAccountId);

        return tradingMovementRepository
                .findByTradingAccountTradingAccountIdOrderByDateAscTradingMovementIdAsc(
                        tradingAccountId)
                .stream()
                .map(TradingMovementMapper::toResponse)
                .toList();
    }

    public TradingMovementResponse create(
            CreateTradingMovementRequest request) {

        TradingAccount account = tradingAccountService.getEntity(
                request.tradingAccountId());

        switch (request.type()) {
            case DEPOSIT ->
                tradingAccountService.applyDeposit(
                        account,
                        request.amount());

            case WITHDRAWAL ->
                tradingAccountService.applyWithdrawal(
                        account,
                        request.amount());
        }

        LocalDate date = request.date() != null
                ? request.date()
                : LocalDate.now();

        TradingMovement movement = TradingMovementMapper.toEntity(
                request,
                account,
                date);

        TradingMovement savedMovement = tradingMovementRepository.save(
                movement);

        return TradingMovementMapper.toResponse(
                savedMovement);
    }

    private TradingMovement getMovement(
            Long tradingMovementId) {

        return tradingMovementRepository
                .findById(tradingMovementId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Movimiento de trading no encontrado"));
    }
}
package com.finance.backend.modules.trading.trade.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.instrument.service.InstrumentService;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.dto.UpdateTradeRequest;
import com.finance.backend.modules.trading.trade.mapper.TradeMapper;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.trade.repository.TradeRepository;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.service.TradingAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TradeService {

        private final TradeRepository tradeRepository;
        private final TradingAccountService tradingAccountService;
        private final InstrumentService instrumentService;

        public TradeService(
                        TradeRepository tradeRepository,
                        TradingAccountService tradingAccountService,
                        InstrumentService instrumentService) {

                this.tradeRepository = tradeRepository;

                this.tradingAccountService = tradingAccountService;

                this.instrumentService = instrumentService;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<TradeResponse> findAll(
                        String email) {

                return tradeRepository
                                .findByTradingAccountUserEmailIgnoreCaseOrderByDateAscTradeIdAsc(
                                                email)
                                .stream()
                                .map(
                                                TradeMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public TradeResponse findById(
                        Long tradeId,
                        String email) {

                return TradeMapper.toResponse(
                                getEntity(
                                                tradeId,
                                                email));
        }

        // ===================
        // FIND BY ACCOUNT
        // ===================

        @Transactional(readOnly = true)
        public List<TradeResponse> findByTradingAccountId(
                        Long tradingAccountId,
                        String email) {

                tradingAccountService.getEntity(
                                tradingAccountId,
                                email);

                return tradeRepository
                                .findByTradingAccountTradingAccountIdAndTradingAccountUserEmailIgnoreCaseOrderByDateAscTradeIdAsc(
                                                tradingAccountId,
                                                email)
                                .stream()
                                .map(
                                                TradeMapper::toResponse)
                                .toList();
        }

        // ===================
        // CREATE
        // ===================

        public TradeResponse create(
                        CreateTradeRequest request,
                        String email) {

                TradingAccount tradingAccount = tradingAccountService.getEntity(
                                request.tradingAccountId(),
                                email);

                Instrument instrument = instrumentService.getEntity(
                                request.instrumentId());

                validateCurrency(
                                tradingAccount,
                                instrument);

                LocalDate date = request.date() != null
                                ? request.date()
                                : LocalDate.now();

                Trade trade = TradeMapper.toEntity(
                                request,
                                tradingAccount,
                                instrument,
                                date);

                Trade savedTrade = tradeRepository.save(
                                trade);

                return TradeMapper.toResponse(
                                savedTrade);
        }

        // ===================
        // UPDATE
        // ===================

        public TradeResponse update(
                        Long tradeId,
                        UpdateTradeRequest request,
                        String email) {

                Trade trade = getEntity(
                                tradeId,
                                email);

                TradingAccount tradingAccount = tradingAccountService.getEntity(
                                request.tradingAccountId(),
                                email);

                Instrument instrument = instrumentService.getEntity(
                                request.instrumentId());

                validateCurrency(
                                tradingAccount,
                                instrument);

                TradeMapper.updateEntity(
                                trade,
                                request,
                                tradingAccount,
                                instrument);

                Trade savedTrade = tradeRepository.save(
                                trade);

                return TradeMapper.toResponse(
                                savedTrade);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long tradeId,
                        String email) {

                Trade trade = getEntity(
                                tradeId,
                                email);

                tradeRepository.delete(
                                trade);
        }

        // ===================
        // VALIDATIONS
        // ===================

        private void validateCurrency(
                        TradingAccount tradingAccount,
                        Instrument instrument) {

                if (!tradingAccount
                                .getCurrency()
                                .equalsIgnoreCase(
                                                instrument.getCurrency())) {

                        throw new BadRequestException(
                                        "La moneda del instrumento no coincide con la cuenta de trading");
                }
        }

        // ===================
        // ENTITY
        // ===================

        @Transactional(readOnly = true)
        public Trade getEntity(
                        Long tradeId,
                        String email) {

                return tradeRepository
                                .findByTradeIdAndTradingAccountUserEmailIgnoreCase(
                                                tradeId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Trade no encontrado"));
        }
}
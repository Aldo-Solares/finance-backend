package com.finance.backend.modules.trading.trade.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.instrument.repository.InstrumentRepository;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.dto.UpdateTradeRequest;
import com.finance.backend.modules.trading.trade.mapper.TradeMapper;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.trade.repository.TradeRepository;
import com.finance.backend.modules.trading.trade.utils.TradeCalculation;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.usertradingaccount.model.UserTradingAccount;
import com.finance.backend.modules.trading.usertradingaccount.repository.UserTradingAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeService {

        private final TradeRepository tradeRepository;
        private final UserTradingAccountRepository userTradingAccountRepository;
        private final InstrumentRepository instrumentRepository;

        public TradeService(
                        TradeRepository tradeRepository,
                        UserTradingAccountRepository userTradingAccountRepository,
                        InstrumentRepository instrumentRepository) {

                this.tradeRepository = tradeRepository;
                this.userTradingAccountRepository = userTradingAccountRepository;
                this.instrumentRepository = instrumentRepository;
        }

        // ===================
        // QUERIES
        // ===================

        @Transactional(readOnly = true)
        public List<TradeResponse> findAll(
                        String email) {

                return tradeRepository
                                .findByUserTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                                                email)
                                .stream()
                                .map(TradeMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TradeResponse findById(
                        Long tradeId,
                        String email) {

                return TradeMapper.toResponse(
                                getEntity(
                                                tradeId,
                                                email));
        }

        @Transactional(readOnly = true)
        public List<TradeResponse> findByAccountId(
                        Long userTradingAccountId,
                        String email) {

                return tradeRepository
                                .findByUserTradingAccountUserTradingAccountIdAndUserTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                                                userTradingAccountId,
                                                email)
                                .stream()
                                .map(TradeMapper::toResponse)
                                .toList();
        }

        // ===================
        // CREATE
        // ===================

        @Transactional
        public TradeResponse create(
                        CreateTradeRequest request,
                        String email) {

                UserTradingAccount userTradingAccount = getUserTradingAccount(
                                request.userTradingAccountId(),
                                email);

                Instrument instrument = getInstrument(
                                request.instrumentId());

                validateCurrency(
                                userTradingAccount,
                                instrument);

                Trade trade = TradeMapper.toEntity(
                                request,
                                userTradingAccount,
                                instrument);

                Trade savedTrade = tradeRepository.save(trade);

                return TradeMapper.toResponse(
                                savedTrade);
        }

        // ===================
        // UPDATE
        // ===================

        @Transactional
        public TradeResponse update(
                        Long tradeId,
                        UpdateTradeRequest request,
                        String email) {

                Trade trade = getEntity(
                                tradeId,
                                email);

                BigDecimal soldQuantity = TradeCalculation.getSoldQuantity(
                                trade);

                if (request.quantity()
                                .compareTo(soldQuantity) < 0) {

                        throw new BadRequestException(
                                        "Trade quantity cannot be lower than already sold quantity");
                }

                UserTradingAccount userTradingAccount = getUserTradingAccount(
                                request.userTradingAccountId(),
                                email);

                Instrument instrument = getInstrument(
                                request.instrumentId());

                validateCurrency(
                                userTradingAccount,
                                instrument);

                TradeMapper.updateEntity(
                                trade,
                                request,
                                userTradingAccount,
                                instrument);

                Trade savedTrade = tradeRepository.save(trade);

                return TradeMapper.toResponse(
                                savedTrade);
        }

        // ===================
        // DELETE
        // ===================

        @Transactional
        public void delete(
                        Long tradeId,
                        String email) {

                Trade trade = getEntity(
                                tradeId,
                                email);

                tradeRepository.delete(trade);
        }

        // ===================
        // ENTITY
        // ===================

        @Transactional(readOnly = true)
        public Trade getEntity(
                        Long tradeId,
                        String email) {

                return tradeRepository
                                .findByTradeIdAndUserTradingAccountUserEmailIgnoreCase(
                                                tradeId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Trade no encontrado"));
        }

        // ===================
        // USER TRADING ACCOUNT
        // ===================

        private UserTradingAccount getUserTradingAccount(
                        Long userTradingAccountId,
                        String email) {

                return userTradingAccountRepository
                                .findByUserTradingAccountIdAndUserEmailIgnoreCase(
                                                userTradingAccountId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Cuenta de trading del usuario no encontrada"));
        }

        // ===================
        // INSTRUMENT
        // ===================

        private Instrument getInstrument(
                        Long instrumentId) {

                return instrumentRepository
                                .findById(instrumentId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Instrumento no encontrado"));
        }

        // ===================
        // VALIDATIONS
        // ===================

        private void validateCurrency(
                        UserTradingAccount userTradingAccount,
                        Instrument instrument) {

                TradingAccount tradingAccount = userTradingAccount.getTradingAccount();

                if (!tradingAccount.getCurrency()
                                .equalsIgnoreCase(
                                                instrument.getCurrency())) {

                        throw new BadRequestException(
                                        "La cuenta de trading y el instrumento deben utilizar la misma moneda");
                }
        }
}
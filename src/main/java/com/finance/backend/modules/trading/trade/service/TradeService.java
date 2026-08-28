package com.finance.backend.modules.trading.trade.service;

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
import com.finance.backend.modules.trading.tradingaccount.repository.TradingAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeService {

        private final TradeRepository tradeRepository;
        private final TradingAccountRepository tradingAccountRepository;
        private final InstrumentRepository instrumentRepository;

        public TradeService(
                        TradeRepository tradeRepository,
                        TradingAccountRepository tradingAccountRepository,
                        InstrumentRepository instrumentRepository) {
                this.tradeRepository = tradeRepository;
                this.tradingAccountRepository = tradingAccountRepository;
                this.instrumentRepository = instrumentRepository;
        }

        @Transactional(readOnly = true)
        public List<TradeResponse> findAll(
                        String email) {
                return tradeRepository
                                .findByTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
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
                        Long tradingAccountId,
                        String email) {
                return tradeRepository
                                .findByTradingAccountTradingAccountIdAndTradingAccountUserEmailIgnoreCaseOrderByPurchaseDateDescTradeIdDesc(
                                                tradingAccountId,
                                                email)
                                .stream()
                                .map(TradeMapper::toResponse)
                                .toList();
        }

        @Transactional
        public TradeResponse create(
                        CreateTradeRequest request,
                        String email) {
                TradingAccount account = getTradingAccount(
                                request.tradingAccountId(),
                                email);

                Instrument instrument = getInstrument(
                                request.instrumentId());

                validateCurrency(
                                account,
                                instrument);

                Trade trade = new Trade();

                trade.setTradingAccount(account);
                trade.setInstrument(instrument);
                trade.setQuantity(request.quantity());
                trade.setPurchasePrice(
                                request.purchasePrice());
                trade.setPurchaseCommission(
                                request.purchaseCommission());
                trade.setPurchaseCommissionRate(
                                request.purchaseCommissionRate());
                trade.setPurchaseDate(
                                request.purchaseDate());

                return TradeMapper.toResponse(
                                tradeRepository.save(trade));
        }

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
                                .compareTo(
                                                soldQuantity) < 0) {
                        throw new IllegalArgumentException(
                                        "Trade quantity cannot be lower than already sold quantity");
                }

                TradingAccount account = getTradingAccount(
                                request.tradingAccountId(),
                                email);

                Instrument instrument = getInstrument(
                                request.instrumentId());

                validateCurrency(
                                account,
                                instrument);

                trade.setTradingAccount(account);
                trade.setInstrument(instrument);
                trade.setQuantity(request.quantity());
                trade.setPurchasePrice(
                                request.purchasePrice());
                trade.setPurchaseCommission(
                                request.purchaseCommission());
                trade.setPurchaseCommissionRate(
                                request.purchaseCommissionRate());
                trade.setPurchaseDate(
                                request.purchaseDate());

                return TradeMapper.toResponse(
                                tradeRepository.save(trade));
        }

        @Transactional
        public void delete(
                        Long tradeId,
                        String email) {
                Trade trade = getEntity(
                                tradeId,
                                email);

                tradeRepository.delete(trade);
        }

        @Transactional(readOnly = true)
        public Trade getEntity(
                        Long tradeId,
                        String email) {
                return tradeRepository
                                .findByTradeIdAndTradingAccountUserEmailIgnoreCase(
                                                tradeId,
                                                email)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Trade not found"));
        }

        private TradingAccount getTradingAccount(
                        Long tradingAccountId,
                        String email) {
                return tradingAccountRepository
                                .findByTradingAccountIdAndUserEmailIgnoreCase(
                                                tradingAccountId,
                                                email)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Trading account not found"));
        }

        private Instrument getInstrument(
                        Long instrumentId) {
                return instrumentRepository
                                .findById(instrumentId)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Instrument not found"));
        }

        private void validateCurrency(
                        TradingAccount account,
                        Instrument instrument) {
                if (!account.getCurrency()
                                .equalsIgnoreCase(
                                                instrument.getCurrency())) {
                        throw new IllegalArgumentException(
                                        "Trading account and instrument must use the same currency");
                }
        }
}
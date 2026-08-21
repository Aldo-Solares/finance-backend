package com.finance.backend.modules.trading.trade.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.instrument.service.InstrumentService;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.mapper.TradeMapper;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.trade.model.TradeSide;
import com.finance.backend.modules.trading.trade.repository.TradeRepository;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingaccount.service.TradingAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TradeService {

        private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.0025");

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
        // QUERIES
        // ===================

        @Transactional(readOnly = true)
        public List<TradeResponse> findAll() {
                return tradeRepository
                                .findAllByOrderByDateAscTradeIdAsc()
                                .stream()
                                .map(TradeMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TradeResponse findById(
                        Long tradeId) {

                return TradeMapper.toResponse(
                                getTrade(tradeId));
        }

        @Transactional(readOnly = true)
        public List<TradeResponse> findByTradingAccountId(
                        Long tradingAccountId) {

                tradingAccountService.getEntity(
                                tradingAccountId);

                return tradeRepository
                                .findByTradingAccountTradingAccountIdOrderByDateAscTradeIdAsc(
                                                tradingAccountId)
                                .stream()
                                .map(TradeMapper::toResponse)
                                .toList();
        }

        // ===================
        // CREATE
        // ===================

        public TradeResponse create(
                        CreateTradeRequest request) {

                TradingAccount tradingAccount = tradingAccountService.getEntity(
                                request.tradingAccountId());

                Instrument instrument = instrumentService.getEntity(
                                request.instrumentId());

                validateCurrency(
                                tradingAccount,
                                instrument);

                BigDecimal grossAmount = calculateGrossAmount(
                                request.quantity(),
                                request.price());

                BigDecimal commission = calculateCommission(
                                grossAmount);

                if (request.side() == TradeSide.BUY) {

                        BigDecimal totalCost = money(
                                        grossAmount.add(
                                                        commission));

                        tradingAccountService.applyBuy(
                                        tradingAccount,
                                        totalCost);

                } else {

                        PositionSnapshot position = calculatePosition(
                                        tradingAccount.getTradingAccountId(),
                                        instrument.getInstrumentId());

                        if (position.quantity()
                                        .compareTo(request.quantity()) < 0) {

                                throw new BadRequestException(
                                                "No hay títulos suficientes para realizar la venta");
                        }

                        BigDecimal costBasisToRelease = calculateCostBasisToRelease(
                                        position,
                                        request.quantity());

                        BigDecimal netProceeds = money(
                                        grossAmount.subtract(
                                                        commission));

                        tradingAccountService.applySell(
                                        tradingAccount,
                                        costBasisToRelease,
                                        netProceeds);
                }

                LocalDate date = request.date() != null
                                ? request.date()
                                : LocalDate.now();

                Trade trade = TradeMapper.toEntity(
                                request,
                                tradingAccount,
                                instrument,
                                commission,
                                date);

                Trade savedTrade = tradeRepository.save(
                                trade);

                return TradeMapper.toResponse(
                                savedTrade);
        }

        // ===================
        // POSITION
        // ===================

        private PositionSnapshot calculatePosition(
                        Long tradingAccountId,
                        Long instrumentId) {

                List<Trade> trades = tradeRepository
                                .findByTradingAccountTradingAccountIdAndInstrumentInstrumentIdOrderByDateAscTradeIdAsc(
                                                tradingAccountId,
                                                instrumentId);

                BigDecimal quantity = BigDecimal.ZERO;

                BigDecimal costBasis = BigDecimal.ZERO;

                for (Trade trade : trades) {

                        if (trade.getSide() == TradeSide.BUY) {

                                BigDecimal grossAmount = calculateGrossAmount(
                                                trade.getQuantity(),
                                                trade.getPrice());

                                BigDecimal totalCost = money(
                                                grossAmount.add(
                                                                trade.getCommission()));

                                quantity = quantity.add(
                                                trade.getQuantity());

                                costBasis = money(
                                                costBasis.add(
                                                                totalCost));

                                continue;
                        }

                        if (quantity.compareTo(
                                        trade.getQuantity()) < 0) {
                                throw new IllegalStateException(
                                                "Historial de trades inconsistente");
                        }

                        BigDecimal releasedCostBasis = calculateCostBasisToRelease(
                                        new PositionSnapshot(
                                                        quantity,
                                                        costBasis),
                                        trade.getQuantity());

                        quantity = quantity.subtract(
                                        trade.getQuantity());

                        costBasis = money(
                                        costBasis.subtract(
                                                        releasedCostBasis));
                }

                return new PositionSnapshot(
                                quantity,
                                costBasis);
        }

        private BigDecimal calculateCostBasisToRelease(
                        PositionSnapshot position,
                        BigDecimal quantityToSell) {

                if (position.quantity()
                                .compareTo(quantityToSell) == 0) {
                        return position.costBasis();
                }

                BigDecimal averageCost = position.costBasis()
                                .divide(
                                                position.quantity(),
                                                16,
                                                RoundingMode.HALF_UP);

                return money(
                                averageCost.multiply(
                                                quantityToSell));
        }

        // ===================
        // CALCULATIONS
        // ===================

        private BigDecimal calculateGrossAmount(
                        BigDecimal quantity,
                        BigDecimal price) {

                return quantity
                                .multiply(price)
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);
        }

        private BigDecimal calculateCommission(
                        BigDecimal grossAmount) {

                return grossAmount
                                .multiply(COMMISSION_RATE)
                                .setScale(
                                                2,
                                                RoundingMode.DOWN);
        }

        private BigDecimal money(
                        BigDecimal value) {

                return value.setScale(
                                2,
                                RoundingMode.HALF_UP);
        }

        // ===================
        // VALIDATIONS
        // ===================

        private void validateCurrency(
                        TradingAccount tradingAccount,
                        Instrument instrument) {

                if (!tradingAccount.getCurrency()
                                .equalsIgnoreCase(
                                                instrument.getCurrency())) {

                        throw new BadRequestException(
                                        "La moneda del instrumento no coincide con la cuenta de trading");
                }
        }

        // ===================
        // INTERNAL
        // ===================

        private Trade getTrade(
                        Long tradeId) {

                return tradeRepository
                                .findById(tradeId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Trade no encontrado"));
        }

        private record PositionSnapshot(
                        BigDecimal quantity,
                        BigDecimal costBasis) {
        }
}
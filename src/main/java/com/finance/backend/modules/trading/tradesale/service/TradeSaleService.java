package com.finance.backend.modules.trading.tradesale.service;

import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.trade.service.TradeService;
import com.finance.backend.modules.trading.trade.utils.TradeCalculation;
import com.finance.backend.modules.trading.tradesale.dto.CreateTradeSaleRequest;
import com.finance.backend.modules.trading.tradesale.dto.TradeSaleResponse;
import com.finance.backend.modules.trading.tradesale.dto.UpdateTradeSaleRequest;
import com.finance.backend.modules.trading.tradesale.mapper.TradeSaleMapper;
import com.finance.backend.modules.trading.tradesale.model.TradeSale;
import com.finance.backend.modules.trading.tradesale.repository.TradeSaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeSaleService {

        private final TradeSaleRepository repository;
        private final TradeService tradeService;

        public TradeSaleService(
                        TradeSaleRepository repository,
                        TradeService tradeService) {
                this.repository = repository;
                this.tradeService = tradeService;
        }

        @Transactional(readOnly = true)
        public List<TradeSaleResponse> findByTradeId(
                        Long tradeId,
                        String email) {
                tradeService.getEntity(
                                tradeId,
                                email);

                return repository
                                .findByTradeTradeIdOrderBySaleDateAscTradeSaleIdAsc(
                                                tradeId)
                                .stream()
                                .map(TradeSaleMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public TradeSaleResponse findById(
                        Long tradeSaleId,
                        String email) {
                return TradeSaleMapper.toResponse(
                                getEntity(
                                                tradeSaleId,
                                                email));
        }

        @Transactional
        public TradeSaleResponse create(
                        CreateTradeSaleRequest request,
                        String email) {
                Trade trade = tradeService.getEntity(
                                request.tradeId(),
                                email);

                BigDecimal remainingQuantity = TradeCalculation
                                .getRemainingQuantity(
                                                trade);

                if (request.quantity()
                                .compareTo(
                                                remainingQuantity) > 0) {
                        throw new IllegalArgumentException(
                                        "Sale quantity exceeds remaining trade quantity");
                }

                if (request.saleDate()
                                .isBefore(
                                                trade.getPurchaseDate())) {
                        throw new IllegalArgumentException(
                                        "Sale date cannot be before purchase date");
                }

                TradeSale sale = new TradeSale();

                sale.setTrade(trade);
                sale.setQuantity(
                                request.quantity());
                sale.setSalePrice(
                                request.salePrice());
                sale.setCommission(
                                request.commission());
                sale.setCommissionRate(
                                request.commissionRate());
                sale.setSaleDate(
                                request.saleDate());

                TradeSale saved = repository.save(sale);

                return TradeSaleMapper.toResponse(
                                saved);
        }

        @Transactional
        public TradeSaleResponse update(
                        Long tradeSaleId,
                        UpdateTradeSaleRequest request,
                        String email) {
                TradeSale sale = getEntity(
                                tradeSaleId,
                                email);

                Trade trade = sale.getTrade();

                BigDecimal soldWithoutCurrent = TradeCalculation
                                .getSoldQuantity(
                                                trade)
                                .subtract(
                                                sale.getQuantity());

                BigDecimal maximumQuantity = trade.getQuantity()
                                .subtract(
                                                soldWithoutCurrent);

                if (request.quantity()
                                .compareTo(
                                                maximumQuantity) > 0) {
                        throw new IllegalArgumentException(
                                        "Sale quantity exceeds remaining trade quantity");
                }

                if (request.saleDate()
                                .isBefore(
                                                trade.getPurchaseDate())) {
                        throw new IllegalArgumentException(
                                        "Sale date cannot be before purchase date");
                }

                sale.setQuantity(
                                request.quantity());
                sale.setSalePrice(
                                request.salePrice());
                sale.setCommission(
                                request.commission());
                sale.setCommissionRate(
                                request.commissionRate());
                sale.setSaleDate(
                                request.saleDate());

                return TradeSaleMapper.toResponse(
                                repository.save(sale));
        }

        @Transactional
        public void delete(
                        Long tradeSaleId,
                        String email) {
                TradeSale sale = getEntity(
                                tradeSaleId,
                                email);

                repository.delete(sale);
        }

        @Transactional(readOnly = true)
        public TradeSale getEntity(
                        Long tradeSaleId,
                        String email) {
                TradeSale sale = repository.findById(
                                tradeSaleId)
                                .orElseThrow(
                                                () -> new EntityNotFoundException(
                                                                "Trade sale not found"));

                tradeService.getEntity(
                                sale.getTrade()
                                                .getTradeId(),
                                email);

                return sale;
        }
}
package com.finance.backend.modules.trading.trade.mapper;

import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.dto.UpdateTradeRequest;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.trade.utils.TradeCalculation;
import com.finance.backend.modules.trading.tradesale.mapper.TradeSaleMapper;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.usertradingaccount.model.UserTradingAccount;

import java.math.BigDecimal;

public final class TradeMapper {

        private TradeMapper() {
        }

        // ===================
        // CREATE REQUEST
        // ===================

        public static Trade toEntity(
                        CreateTradeRequest request,
                        UserTradingAccount userTradingAccount,
                        Instrument instrument) {

                Trade trade = new Trade();

                trade.setUserTradingAccount(userTradingAccount);
                trade.setInstrument(instrument);
                trade.setQuantity(request.quantity());
                trade.setPurchasePrice(request.purchasePrice());
                trade.setPurchaseCommission(request.purchaseCommission());
                trade.setPurchaseCommissionRate(request.purchaseCommissionRate());
                trade.setPurchaseDate(request.purchaseDate());

                return trade;
        }

        // ===================
        // UPDATE REQUEST
        // ===================

        public static void updateEntity(
                        Trade trade,
                        UpdateTradeRequest request,
                        UserTradingAccount userTradingAccount,
                        Instrument instrument) {

                trade.setUserTradingAccount(userTradingAccount);
                trade.setInstrument(instrument);
                trade.setQuantity(request.quantity());
                trade.setPurchasePrice(request.purchasePrice());
                trade.setPurchaseCommission(request.purchaseCommission());
                trade.setPurchaseCommissionRate(request.purchaseCommissionRate());
                trade.setPurchaseDate(request.purchaseDate());
        }

        // ===================
        // RESPONSE
        // ===================

        public static TradeResponse toResponse(
                        Trade trade) {

                UserTradingAccount userTradingAccount = trade.getUserTradingAccount();

                TradingAccount tradingAccount = userTradingAccount.getTradingAccount();

                Instrument instrument = trade.getInstrument();

                BigDecimal expectedPurchaseCommission = TradeCalculation.calculateExpectedCommission(
                                trade.getQuantity(),
                                trade.getPurchasePrice(),
                                trade.getPurchaseCommissionRate());

                return new TradeResponse(
                                trade.getTradeId(),

                                userTradingAccount.getUserTradingAccountId(),

                                tradingAccount.getTradingAccountId(),

                                tradingAccount.getName(),

                                instrument.getInstrumentId(),

                                instrument.getSymbol(),

                                instrument.getName(),

                                tradingAccount.getCurrency(),

                                trade.getQuantity(),

                                trade.getPurchasePrice(),

                                trade.getPurchaseCommission(),

                                trade.getPurchaseCommissionRate(),

                                expectedPurchaseCommission,

                                TradeCalculation.isCommissionValid(
                                                trade.getPurchaseCommission(),
                                                expectedPurchaseCommission),

                                trade.getPurchaseDate(),

                                TradeCalculation.getPurchaseGrossAmount(
                                                trade),

                                TradeCalculation.getPurchaseTotalCost(
                                                trade),

                                TradeCalculation.getSoldQuantity(
                                                trade),

                                TradeCalculation.getRemainingQuantity(
                                                trade),

                                TradeCalculation.getRemainingCost(
                                                trade),

                                TradeCalculation.getTotalSaleAmount(
                                                trade),

                                TradeCalculation.getTotalSaleCommissions(
                                                trade),

                                TradeCalculation.getRealizedProfit(
                                                trade),

                                TradeCalculation.getStatus(
                                                trade),

                                trade.getSales()
                                                .stream()
                                                .map(TradeSaleMapper::toResponse)
                                                .toList());
        }
}
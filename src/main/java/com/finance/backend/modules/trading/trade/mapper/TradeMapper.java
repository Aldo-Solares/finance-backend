package com.finance.backend.modules.trading.trade.mapper;

import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.trade.utils.TradeCalculation;
import com.finance.backend.modules.trading.tradesale.mapper.TradeSaleMapper;

public final class TradeMapper {

        private TradeMapper() {
        }

        public static TradeResponse toResponse(
                        Trade trade) {
                var expectedPurchaseCommission = TradeCalculation.calculateExpectedCommission(
                                trade.getQuantity(),
                                trade.getPurchasePrice(),
                                trade.getPurchaseCommissionRate());

                return new TradeResponse(
                                trade.getTradeId(),

                                trade.getTradingAccount()
                                                .getTradingAccountId(),

                                trade.getTradingAccount()
                                                .getName(),

                                trade.getInstrument()
                                                .getInstrumentId(),

                                trade.getInstrument()
                                                .getSymbol(),

                                trade.getInstrument()
                                                .getName(),

                                trade.getTradingAccount()
                                                .getCurrency(),

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
                                                .map(
                                                                TradeSaleMapper::toResponse)
                                                .toList());
        }
}
package com.finance.backend.modules.trading.tradesale.mapper;

import com.finance.backend.modules.trading.trade.utils.TradeCalculation;
import com.finance.backend.modules.trading.tradesale.dto.TradeSaleResponse;
import com.finance.backend.modules.trading.tradesale.model.TradeSale;

public final class TradeSaleMapper {

        private TradeSaleMapper() {
        }

        public static TradeSaleResponse toResponse(
                        TradeSale sale) {
                var expectedCommission = TradeCalculation.calculateExpectedCommission(
                                sale.getQuantity(),
                                sale.getSalePrice(),
                                sale.getCommissionRate());

                return new TradeSaleResponse(
                                sale.getTradeSaleId(),
                                sale.getTrade().getTradeId(),
                                sale.getQuantity(),
                                sale.getSalePrice(),
                                sale.getCommission(),
                                sale.getCommissionRate(),
                                expectedCommission,
                                TradeCalculation.isCommissionValid(
                                                sale.getCommission(),
                                                expectedCommission),
                                sale.getSaleDate(),
                                TradeCalculation.getSaleGrossAmount(
                                                sale),
                                TradeCalculation.getSaleNetAmount(
                                                sale),
                                TradeCalculation.getSaleCostBasis(
                                                sale),
                                TradeCalculation.getSaleRealizedProfit(
                                                sale));
        }
}
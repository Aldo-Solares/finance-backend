package com.finance.backend.modules.trading.trade.utils;

import com.finance.backend.modules.trading.trade.model.TradeStatus;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.tradesale.model.TradeSale;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TradeCalculation {

        private static final BigDecimal ZERO = BigDecimal.ZERO;

        private static final BigDecimal HUNDRED = new BigDecimal("100");

        private static final int SCALE = 8;

        private TradeCalculation() {
        }

        public static BigDecimal calculateExpectedCommission(
                        BigDecimal quantity,
                        BigDecimal price,
                        BigDecimal commissionRate) {
                return quantity
                                .multiply(price)
                                .multiply(commissionRate)
                                .divide(
                                                HUNDRED,
                                                SCALE,
                                                RoundingMode.HALF_UP);
        }

        public static boolean isCommissionValid(
                        BigDecimal actual,
                        BigDecimal expected) {
                return actual.compareTo(expected) == 0;
        }

        public static BigDecimal getPurchaseGrossAmount(
                        Trade trade) {
                return trade.getQuantity()
                                .multiply(trade.getPurchasePrice());
        }

        public static BigDecimal getPurchaseTotalCost(
                        Trade trade) {
                return getPurchaseGrossAmount(trade)
                                .add(trade.getPurchaseCommission());
        }

        public static BigDecimal getUnitCost(
                        Trade trade) {
                return getPurchaseTotalCost(trade)
                                .divide(
                                                trade.getQuantity(),
                                                SCALE,
                                                RoundingMode.HALF_UP);
        }

        public static BigDecimal getSoldQuantity(
                        Trade trade) {
                return trade.getSales()
                                .stream()
                                .map(TradeSale::getQuantity)
                                .reduce(
                                                ZERO,
                                                BigDecimal::add);
        }

        public static BigDecimal getRemainingQuantity(
                        Trade trade) {
                return trade.getQuantity()
                                .subtract(
                                                getSoldQuantity(trade));
        }

        public static BigDecimal getRemainingCost(
                        Trade trade) {
                return getRemainingQuantity(trade)
                                .multiply(
                                                getUnitCost(trade));
        }

        public static BigDecimal getSaleGrossAmount(
                        TradeSale sale) {
                return sale.getQuantity()
                                .multiply(sale.getSalePrice());
        }

        public static BigDecimal getSaleNetAmount(
                        TradeSale sale) {
                return getSaleGrossAmount(sale)
                                .subtract(sale.getCommission());
        }

        public static BigDecimal getSaleCostBasis(
                        TradeSale sale) {
                return sale.getQuantity()
                                .multiply(
                                                getUnitCost(
                                                                sale.getTrade()));
        }

        public static BigDecimal getSaleRealizedProfit(
                        TradeSale sale) {
                return getSaleNetAmount(sale)
                                .subtract(
                                                getSaleCostBasis(sale));
        }

        public static BigDecimal getTotalSaleAmount(
                        Trade trade) {
                return trade.getSales()
                                .stream()
                                .map(
                                                TradeCalculation::getSaleGrossAmount)
                                .reduce(
                                                ZERO,
                                                BigDecimal::add);
        }

        public static BigDecimal getTotalSaleCommissions(
                        Trade trade) {
                return trade.getSales()
                                .stream()
                                .map(TradeSale::getCommission)
                                .reduce(
                                                ZERO,
                                                BigDecimal::add);
        }

        public static BigDecimal getRealizedProfit(
                        Trade trade) {
                return trade.getSales()
                                .stream()
                                .map(
                                                TradeCalculation::getSaleRealizedProfit)
                                .reduce(
                                                ZERO,
                                                BigDecimal::add);
        }

        public static TradeStatus getStatus(
                        Trade trade) {
                BigDecimal soldQuantity = getSoldQuantity(trade);

                if (soldQuantity.compareTo(ZERO) == 0) {
                        return TradeStatus.OPEN;
                }

                if (soldQuantity.compareTo(
                                trade.getQuantity()) < 0) {
                        return TradeStatus.PARTIALLY_SOLD;
                }

                return TradeStatus.CLOSED;
        }
}
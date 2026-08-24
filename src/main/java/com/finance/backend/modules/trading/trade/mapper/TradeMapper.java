package com.finance.backend.modules.trading.trade.mapper;

import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.dto.UpdateTradeRequest;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public final class TradeMapper {

        private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

        private static final BigDecimal COMMISSION_TOLERANCE = new BigDecimal("0.01");

        private TradeMapper() {
        }

        public static Trade toEntity(
                        CreateTradeRequest request,
                        TradingAccount tradingAccount,
                        Instrument instrument,
                        LocalDate date) {

                Trade trade = new Trade();

                trade.setTradingAccount(
                                tradingAccount);

                trade.setInstrument(
                                instrument);

                trade.setSide(
                                request.side());

                trade.setQuantity(
                                request.quantity());

                trade.setPrice(
                                request.price());

                trade.setCommission(
                                request.commission());

                trade.setCommissionRate(
                                request.commissionRate());

                trade.setDate(
                                date);

                return trade;
        }

        public static void updateEntity(
                        Trade trade,
                        UpdateTradeRequest request,
                        TradingAccount tradingAccount,
                        Instrument instrument) {

                trade.setTradingAccount(
                                tradingAccount);

                trade.setInstrument(
                                instrument);

                trade.setSide(
                                request.side());

                trade.setQuantity(
                                request.quantity());

                trade.setPrice(
                                request.price());

                trade.setCommission(
                                request.commission());

                trade.setCommissionRate(
                                request.commissionRate());

                trade.setDate(
                                request.date());
        }

        public static TradeResponse toResponse(
                        Trade trade) {

                BigDecimal expectedCommission = calculateExpectedCommission(
                                trade);

                boolean commissionValid = isCommissionValid(
                                trade,
                                expectedCommission);

                return new TradeResponse(
                                trade.getTradeId(),
                                trade
                                                .getTradingAccount()
                                                .getTradingAccountId(),
                                trade
                                                .getInstrument()
                                                .getInstrumentId(),
                                trade.getSide(),
                                trade.getQuantity(),
                                trade.getPrice(),
                                trade.getCommission(),
                                trade.getCommissionRate(),
                                expectedCommission,
                                commissionValid,
                                trade.getDate());
        }

        private static BigDecimal calculateExpectedCommission(
                        Trade trade) {

                BigDecimal grossAmount = trade.getQuantity()
                                .multiply(
                                                trade.getPrice())
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                BigDecimal rate = trade.getCommissionRate()
                                .divide(
                                                ONE_HUNDRED,
                                                10,
                                                RoundingMode.HALF_UP);

                return grossAmount
                                .multiply(rate)
                                .setScale(
                                                2,
                                                RoundingMode.DOWN);
        }

        private static boolean isCommissionValid(
                        Trade trade,
                        BigDecimal expectedCommission) {

                BigDecimal difference = trade.getCommission()
                                .subtract(
                                                expectedCommission)
                                .abs();

                return difference.compareTo(
                                COMMISSION_TOLERANCE) <= 0;
        }
}
package com.finance.backend.modules.trading.trade.mapper;

import com.finance.backend.modules.trading.instrument.model.Instrument;
import com.finance.backend.modules.trading.trade.dto.CreateTradeRequest;
import com.finance.backend.modules.trading.trade.dto.TradeResponse;
import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TradeMapper {

    private TradeMapper() {
    }

    public static Trade toEntity(
            CreateTradeRequest request,
            TradingAccount tradingAccount,
            Instrument instrument,
            BigDecimal commission,
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
                commission);

        trade.setDate(
                date);

        return trade;
    }

    public static TradeResponse toResponse(
            Trade trade) {

        return new TradeResponse(
                trade.getTradeId(),
                trade.getTradingAccount()
                        .getTradingAccountId(),
                trade.getInstrument()
                        .getInstrumentId(),
                trade.getSide(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getCommission(),
                trade.getDate());
    }
}
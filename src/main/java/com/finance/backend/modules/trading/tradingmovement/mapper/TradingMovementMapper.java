package com.finance.backend.modules.trading.tradingmovement.mapper;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingmovement.dto.CreateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.dto.TradingMovementResponse;
import com.finance.backend.modules.trading.tradingmovement.model.TradingMovement;

import java.time.LocalDate;

public final class TradingMovementMapper {

    private TradingMovementMapper() {
    }

    public static TradingMovement toEntity(
            CreateTradingMovementRequest request,
            TradingAccount account,
            LocalDate date) {

        TradingMovement movement = new TradingMovement();

        movement.setTradingAccount(account);
        movement.setType(request.type());
        movement.setAmount(request.amount());
        movement.setDate(date);
        movement.setNotes(request.notes());

        return movement;
    }

    public static TradingMovementResponse toResponse(
            TradingMovement movement) {

        return new TradingMovementResponse(
                movement.getTradingMovementId(),
                movement.getTradingAccount()
                        .getTradingAccountId(),
                movement.getType(),
                movement.getAmount(),
                movement.getDate(),
                movement.getNotes());
    }
}
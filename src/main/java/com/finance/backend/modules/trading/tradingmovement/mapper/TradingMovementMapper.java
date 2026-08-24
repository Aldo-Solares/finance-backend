package com.finance.backend.modules.trading.tradingmovement.mapper;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.tradingmovement.dto.CreateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.dto.TradingMovementResponse;
import com.finance.backend.modules.trading.tradingmovement.dto.UpdateTradingMovementRequest;
import com.finance.backend.modules.trading.tradingmovement.model.TradingMovement;

public final class TradingMovementMapper {

        private TradingMovementMapper() {
        }

        public static TradingMovement toEntity(
                        CreateTradingMovementRequest request,
                        TradingAccount tradingAccount) {

                TradingMovement movement = new TradingMovement();

                movement.setTradingAccount(
                                tradingAccount);

                movement.setType(
                                request.type());

                movement.setAmount(
                                request.amount());

                movement.setDate(
                                request.date());

                movement.setNotes(
                                request.notes());

                return movement;
        }

        public static void updateEntity(
                        TradingMovement movement,
                        UpdateTradingMovementRequest request,
                        TradingAccount tradingAccount) {

                movement.setTradingAccount(
                                tradingAccount);

                movement.setType(
                                request.type());

                movement.setAmount(
                                request.amount());

                movement.setDate(
                                request.date());

                movement.setNotes(
                                request.notes());
        }

        public static TradingMovementResponse toResponse(
                        TradingMovement movement) {

                return new TradingMovementResponse(
                                movement.getTradingMovementId(),
                                movement
                                                .getTradingAccount()
                                                .getTradingAccountId(),
                                movement
                                                .getTradingAccount()
                                                .getName(),
                                movement
                                                .getTradingAccount()
                                                .getCurrency(),
                                movement.getType(),
                                movement.getAmount(),
                                movement.getDate(),
                                movement.getNotes());
        }
}
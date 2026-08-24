package com.finance.backend.modules.trading.tradingaccount.mapper;

import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.user.model.User;

public final class TradingAccountMapper {

    private TradingAccountMapper() {
    }

    public static TradingAccount toEntity(
            CreateTradingAccountRequest request,
            User user) {

        TradingAccount tradingAccount = new TradingAccount();

        tradingAccount.setUser(
                user);

        tradingAccount.setName(
                request.name());

        tradingAccount.setCurrency(
                request.currency());

        return tradingAccount;
    }

    public static void updateEntity(
            TradingAccount tradingAccount,
            UpdateTradingAccountRequest request) {

        tradingAccount.setName(
                request.name());

        tradingAccount.setCurrency(
                request.currency());
    }

    public static TradingAccountResponse toResponse(
            TradingAccount tradingAccount) {

        return new TradingAccountResponse(
                tradingAccount.getTradingAccountId(),
                tradingAccount.getUser().getUserId(),
                tradingAccount.getName(),
                tradingAccount.getCurrency());
    }
}
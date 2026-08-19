package com.finance.backend.modules.trading.tradingaccount.mapper;

import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.user.model.User;

import java.math.BigDecimal;

public final class TradingAccountMapper {

    private TradingAccountMapper() {
    }

    public static TradingAccount toEntity(
            CreateTradingAccountRequest request,
            User user) {

        TradingAccount account = new TradingAccount();

        account.setUser(user);
        account.setName(request.name());
        account.setCurrency(request.currency());
        account.setBalance(BigDecimal.ZERO);
        account.setInvestedAmount(BigDecimal.ZERO);
        account.setAvailableAmount(BigDecimal.ZERO);

        return account;
    }

    public static void updateEntity(
            TradingAccount account,
            UpdateTradingAccountRequest request) {

        account.setName(request.name());
        account.setCurrency(request.currency());
    }

    public static TradingAccountResponse toResponse(
            TradingAccount account) {

        return new TradingAccountResponse(
                account.getTradingAccountId(),
                account.getUser().getUserId(),
                account.getName(),
                account.getCurrency(),
                account.getBalance(),
                account.getInvestedAmount(),
                account.getAvailableAmount());
    }
}
package com.finance.backend.modules.trading.usertradingaccount.mapper;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.trading.usertradingaccount.dto.CreateUserTradingAccountRequest;
import com.finance.backend.modules.trading.usertradingaccount.dto.UpdateUserTradingAccountRequest;
import com.finance.backend.modules.trading.usertradingaccount.dto.UserTradingAccountResponse;
import com.finance.backend.modules.trading.usertradingaccount.model.UserTradingAccount;
import com.finance.backend.modules.user.model.User;

public final class UserTradingAccountMapper {

    private UserTradingAccountMapper() {
    }

    // ===================
    // CREATE REQUEST
    // ===================

    public static UserTradingAccount toEntity(
            CreateUserTradingAccountRequest request,
            User user,
            TradingAccount tradingAccount) {

        UserTradingAccount userTradingAccount = new UserTradingAccount();

        userTradingAccount.setUser(user);
        userTradingAccount.setTradingAccount(tradingAccount);
        userTradingAccount.setAlias(request.alias());
        userTradingAccount.setAccountNumber(request.accountNumber());
        userTradingAccount.setActive(request.active());

        return userTradingAccount;
    }

    // ===================
    // UPDATE REQUEST
    // ===================

    public static void updateEntity(
            UserTradingAccount userTradingAccount,
            UpdateUserTradingAccountRequest request,
            TradingAccount tradingAccount) {

        userTradingAccount.setTradingAccount(tradingAccount);
        userTradingAccount.setAlias(request.alias());
        userTradingAccount.setAccountNumber(request.accountNumber());
        userTradingAccount.setActive(request.active());
    }

    // ===================
    // RESPONSE
    // ===================

    public static UserTradingAccountResponse toResponse(
            UserTradingAccount userTradingAccount) {

        TradingAccount tradingAccount = userTradingAccount.getTradingAccount();

        return new UserTradingAccountResponse(
                userTradingAccount.getUserTradingAccountId(),
                tradingAccount.getTradingAccountId(),
                tradingAccount.getInstitution(),
                tradingAccount.getName(),
                tradingAccount.getAccountType(),
                tradingAccount.getCurrency(),
                userTradingAccount.getAlias(),
                userTradingAccount.getAccountNumber(),
                userTradingAccount.getActive());
    }
}
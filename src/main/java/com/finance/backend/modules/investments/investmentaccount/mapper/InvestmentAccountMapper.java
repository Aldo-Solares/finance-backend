package com.finance.backend.modules.investments.investmentaccount.mapper;

import com.finance.backend.modules.investments.investmentaccount.dto.CreateInvestmentAccountRequest;
import com.finance.backend.modules.investments.investmentaccount.dto.InvestmentAccountResponse;
import com.finance.backend.modules.investments.investmentaccount.dto.UpdateInvestmentAccountRequest;
import com.finance.backend.modules.investments.investmentaccount.model.InvestmentAccount;
import com.finance.backend.modules.user.model.User;

import java.math.BigDecimal;

public final class InvestmentAccountMapper {

    private InvestmentAccountMapper() {
    }

    public static InvestmentAccount toEntity(
            CreateInvestmentAccountRequest request,
            User user) {

        InvestmentAccount account = new InvestmentAccount();

        account.setUser(user);
        account.setName(request.name());
        account.setCurrency(request.currency());
        account.setBalance(BigDecimal.ZERO);

        return account;
    }

    public static void updateEntity(
            InvestmentAccount account,
            UpdateInvestmentAccountRequest request) {

        account.setName(request.name());
        account.setCurrency(request.currency());
    }

    public static InvestmentAccountResponse toResponse(
            InvestmentAccount account) {

        return new InvestmentAccountResponse(
                account.getInvestmentAccountId(),
                account.getUser().getUserId(),
                account.getName(),
                account.getCurrency(),
                account.getBalance());
    }
}
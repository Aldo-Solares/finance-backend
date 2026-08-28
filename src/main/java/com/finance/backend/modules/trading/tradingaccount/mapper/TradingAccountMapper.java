package com.finance.backend.modules.trading.tradingaccount.mapper;

import com.finance.backend.modules.trading.tradingaccount.dto.CreateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.dto.TradingAccountResponse;
import com.finance.backend.modules.trading.tradingaccount.dto.UpdateTradingAccountRequest;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;

public final class TradingAccountMapper {

        private TradingAccountMapper() {
        }

        // ===================
        // CREATE REQUEST
        // ===================

        public static TradingAccount toEntity(
                        CreateTradingAccountRequest request) {

                TradingAccount tradingAccount = new TradingAccount();

                tradingAccount.setInstitution(request.institution());
                tradingAccount.setName(request.name());
                tradingAccount.setAccountType(request.accountType());
                tradingAccount.setCurrency(request.currency());
                tradingAccount.setActive(request.active());

                return tradingAccount;
        }

        // ===================
        // UPDATE REQUEST
        // ===================

        public static void updateEntity(
                        TradingAccount tradingAccount,
                        UpdateTradingAccountRequest request) {

                tradingAccount.setInstitution(request.institution());
                tradingAccount.setName(request.name());
                tradingAccount.setAccountType(request.accountType());
                tradingAccount.setCurrency(request.currency());
                tradingAccount.setActive(request.active());
        }

        // ===================
        // RESPONSE
        // ===================

        public static TradingAccountResponse toResponse(
                        TradingAccount tradingAccount) {

                return new TradingAccountResponse(
                                tradingAccount.getTradingAccountId(),
                                tradingAccount.getInstitution(),
                                tradingAccount.getName(),
                                tradingAccount.getAccountType(),
                                tradingAccount.getCurrency(),
                                tradingAccount.getActive());
        }
}
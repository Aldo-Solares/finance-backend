package com.finance.backend.modules.trading.tradingaccount.mapper;

import com.finance.backend.modules.catalogs.currency.model.Currency;
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
                        CreateTradingAccountRequest request,
                        Currency currency) {

                TradingAccount tradingAccount = new TradingAccount();

                tradingAccount.setInstitution(request.institution());
                tradingAccount.setName(request.name());
                tradingAccount.setCurrency(currency);
                tradingAccount.setActive(request.active());

                return tradingAccount;
        }

        // ===================
        // UPDATE REQUEST
        // ===================

        public static void updateEntity(
                        TradingAccount tradingAccount,
                        UpdateTradingAccountRequest request,
                        Currency currency) {

                tradingAccount.setInstitution(request.institution());
                tradingAccount.setName(request.name());
                tradingAccount.setCurrency(currency);
                tradingAccount.setActive(request.active());
        }

        // ===================
        // RESPONSE
        // ===================

        public static TradingAccountResponse toResponse(
                        TradingAccount tradingAccount) {

                Currency currency = tradingAccount.getCurrency();

                return new TradingAccountResponse(
                                tradingAccount.getTradingAccountId(),
                                tradingAccount.getInstitution(),
                                tradingAccount.getName(),
                                currency.getCurrencyId(),
                                currency.getCode(),
                                currency.getSymbol(),
                                tradingAccount.getActive());
        }
}
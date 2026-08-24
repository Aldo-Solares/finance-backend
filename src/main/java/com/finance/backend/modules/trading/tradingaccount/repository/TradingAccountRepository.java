package com.finance.backend.modules.trading.tradingaccount.repository;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradingAccountRepository
                extends JpaRepository<TradingAccount, Long> {

        List<TradingAccount> findByUserEmailIgnoreCaseOrderByTradingAccountIdAsc(
                        String email);

        Optional<TradingAccount> findByTradingAccountIdAndUserEmailIgnoreCase(
                        Long tradingAccountId,
                        String email);
}
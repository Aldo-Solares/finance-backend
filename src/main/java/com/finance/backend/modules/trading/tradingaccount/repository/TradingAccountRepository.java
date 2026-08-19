package com.finance.backend.modules.trading.tradingaccount.repository;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingAccountRepository
        extends JpaRepository<TradingAccount, Long> {
}
package com.finance.backend.modules.trading.usertradingaccount.model;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_trading_accounts")
public class UserTradingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_trading_account_id")
    private Long userTradingAccountId;

    // ===================
    // USER
    // ===================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===================
    // TRADING ACCOUNT
    // ===================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trading_account_id", nullable = false)
    private TradingAccount tradingAccount;

    // ===================
    // ID
    // ===================

    public Long getUserTradingAccountId() {
        return userTradingAccountId;
    }

    public void setUserTradingAccountId(Long userTradingAccountId) {
        this.userTradingAccountId = userTradingAccountId;
    }

    // ===================
    // USER
    // ===================

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ===================
    // TRADING ACCOUNT
    // ===================

    public TradingAccount getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(TradingAccount tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

}
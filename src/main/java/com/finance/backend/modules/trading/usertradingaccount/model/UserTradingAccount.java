package com.finance.backend.modules.trading.usertradingaccount.model;

import com.finance.backend.modules.trading.trade.model.Trade;
import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.user.model.User;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_trading_accounts")
public class UserTradingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_trading_account_id")
    private Long userTradingAccountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trading_account_id", nullable = false)
    private TradingAccount tradingAccount;

    @OneToMany(mappedBy = "userTradingAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> trades = new ArrayList<>();

    public UserTradingAccount() {
    }

    public Long getUserTradingAccountId() {
        return userTradingAccountId;
    }

    public void setUserTradingAccountId(Long userTradingAccountId) {
        this.userTradingAccountId = userTradingAccountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TradingAccount getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(TradingAccount tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }
}
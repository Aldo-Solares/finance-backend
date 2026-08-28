package com.finance.backend.modules.trading.usertradingaccount.model;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import com.finance.backend.modules.user.model.User;
import jakarta.persistence.*;

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

    @Column
    private String alias;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(nullable = false)
    private Boolean active;

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
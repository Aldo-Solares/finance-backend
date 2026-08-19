package com.finance.backend.modules.investments.investmentaccount.model;

import com.finance.backend.modules.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "investment_accounts")
public class InvestmentAccount {

    // ===================
    // FIELDS
    // ===================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_account_id")
    private Long investmentAccountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // ===================
    // CONSTRUCTOR
    // ===================

    public InvestmentAccount() {
    }

    // ===================
    // GETTERS / SETTERS
    // ===================

    public Long getInvestmentAccountId() {
        return investmentAccountId;
    }

    public void setInvestmentAccountId(Long investmentAccountId) {
        this.investmentAccountId = investmentAccountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
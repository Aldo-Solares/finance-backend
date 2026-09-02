package com.finance.backend.modules.trading.tradingaccount.model;

import com.finance.backend.modules.catalogs.currency.model.Currency;
import jakarta.persistence.*;

@Entity
@Table(name = "trading_accounts")
public class TradingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trading_account_id")
    private Long tradingAccountId;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private Boolean active;

    public TradingAccount() {
    }

    // ===================
    // ID
    // ===================

    public Long getTradingAccountId() {
        return tradingAccountId;
    }

    public void setTradingAccountId(Long tradingAccountId) {
        this.tradingAccountId = tradingAccountId;
    }

    // ===================
    // INSTITUTION
    // ===================

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    // ===================
    // NAME
    // ===================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ===================
    // CURRENCY
    // ===================

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    // ===================
    // ACTIVE
    // ===================

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
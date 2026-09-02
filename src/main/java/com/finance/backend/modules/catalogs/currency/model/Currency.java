package com.finance.backend.modules.catalogs.currency.model;

import jakarta.persistence.*;

@Entity
@Table(name = "currencies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_currency_code", columnNames = "code")
})
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private Long currencyId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String symbol;

    public Currency() {
    }

    // ===================
    // GETTERS AND SETTERS
    // ===================

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
package com.finance.backend.modules.trading.tradingmovement.model;

import com.finance.backend.modules.trading.tradingaccount.model.TradingAccount;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trading_movements")
public class TradingMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trading_movement_id")
    private Long tradingMovementId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trading_account_id", nullable = false)
    private TradingAccount tradingAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradingMovementType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public TradingMovement() {
    }

    public Long getTradingMovementId() {
        return tradingMovementId;
    }

    public void setTradingMovementId(Long tradingMovementId) {
        this.tradingMovementId = tradingMovementId;
    }

    public TradingAccount getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(
            TradingAccount tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public TradingMovementType getType() {
        return type;
    }

    public void setType(TradingMovementType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
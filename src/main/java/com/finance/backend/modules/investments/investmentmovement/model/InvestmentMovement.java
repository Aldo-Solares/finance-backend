package com.finance.backend.modules.investments.investmentmovement.model;

import com.finance.backend.modules.investments.investmentaccount.model.InvestmentAccount;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investment_movements")
public class InvestmentMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_movement_id")
    private Long investmentMovementId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "investment_account_id", nullable = false)
    private InvestmentAccount investmentAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentMovementType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public InvestmentMovement() {
    }

    public Long getInvestmentMovementId() {
        return investmentMovementId;
    }

    public void setInvestmentMovementId(Long investmentMovementId) {
        this.investmentMovementId = investmentMovementId;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAccount;
    }

    public void setInvestmentAccount(
            InvestmentAccount investmentAccount) {
        this.investmentAccount = investmentAccount;
    }

    public InvestmentMovementType getType() {
        return type;
    }

    public void setType(InvestmentMovementType type) {
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
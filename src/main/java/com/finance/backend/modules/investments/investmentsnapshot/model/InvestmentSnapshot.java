package com.finance.backend.modules.investments.investmentsnapshot.model;

import com.finance.backend.modules.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investment_snapshots")
public class InvestmentSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_snapshot_id")
    private Long investmentSnapshotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal contribution = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal withdrawal = BigDecimal.ZERO;

    @Column(name = "generated_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal generatedAmount = BigDecimal.ZERO;

    public InvestmentSnapshot() {
    }

    public Long getInvestmentSnapshotId() {
        return investmentSnapshotId;
    }

    public void setInvestmentSnapshotId(Long investmentSnapshotId) {
        this.investmentSnapshotId = investmentSnapshotId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(BigDecimal contribution) {
        this.contribution = contribution;
    }

    public BigDecimal getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(BigDecimal withdrawal) {
        this.withdrawal = withdrawal;
    }

    public BigDecimal getGeneratedAmount() {
        return generatedAmount;
    }

    public void setGeneratedAmount(BigDecimal generatedAmount) {
        this.generatedAmount = generatedAmount;
    }
}
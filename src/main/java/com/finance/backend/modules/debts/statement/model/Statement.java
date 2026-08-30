package com.finance.backend.modules.debts.statement.model;

import com.finance.backend.modules.debts.usercard.model.UserCard;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "statements", uniqueConstraints = {
        @UniqueConstraint(name = "uq_statement_period", columnNames = {
                "user_card_id",
                "year",
                "month"
        })
})
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statement_id")
    private Long statementId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_card_id", nullable = false)
    private UserCard userCard;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatementStatus status;

    @Column(nullable = false)
    private Boolean paid = false;

    private String notes;

    public Statement() {
    }

    public Long getStatementId() {
        return statementId;
    }

    public void setStatementId(
            Long statementId) {
        this.statementId = statementId;
    }

    public UserCard getUserCard() {
        return userCard;
    }

    public void setUserCard(
            UserCard userCard) {
        this.userCard = userCard;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(
            Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(
            Integer month) {
        this.month = month;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public StatementStatus getStatus() {
        return status;
    }

    public void setStatus(
            StatementStatus status) {
        this.status = status;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(
            Boolean paid) {
        this.paid = paid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {
        this.notes = notes;
    }
}
package com.finance.backend.modules.debts.statemententry.model;

import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.statement.model.Statement;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "statement_entries")
public class StatementEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "statement_id", nullable = false)
    private Statement statement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Concept category;

    @Column(name = "debtor", nullable = false)
    private String debtor;

    @Column(name = "description")
    private String description;

    @Column(name = "merchant")
    private String merchant;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private StatementEntryType entryType;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "total_installments")
    private Integer totalInstallments;

    @Column(name = "purchase_amount", precision = 14, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(name = "remaining_installments")
    private Integer remainingInstallments;

    @Column(name = "remaining_installments_amount", precision = 14, scale = 2)
    private BigDecimal remainingInstallmentsAmount;

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public Concept getCategory() {
        return category;
    }

    public void setCategory(Concept category) {
        this.category = category;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtor) {
        this.debtor = debtor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public StatementEntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(StatementEntryType entryType) {
        this.entryType = entryType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public Integer getTotalInstallments() {
        return totalInstallments;
    }

    public void setTotalInstallments(Integer totalInstallments) {
        this.totalInstallments = totalInstallments;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public Integer getRemainingInstallments() {
        return remainingInstallments;
    }

    public void setRemainingInstallments(Integer remainingInstallments) {
        this.remainingInstallments = remainingInstallments;
    }

    public BigDecimal getRemainingInstallmentsAmount() {
        return remainingInstallmentsAmount;
    }

    public void setRemainingInstallmentsAmount(BigDecimal remainingInstallmentsAmount) {
        this.remainingInstallmentsAmount = remainingInstallmentsAmount;
    }
}
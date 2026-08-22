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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id", nullable = false)
    private Statement statement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    @Column(name = "debtor", nullable = false, length = 100)
    private String debtor;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "installment_amount", precision = 14, scale = 2)
    private BigDecimal installmentAmount;

    private Boolean paid;

    @Column(name = "msi_current")
    private Integer msiCurrent;

    @Column(name = "msi_total")
    private Integer msiTotal;

    @Column(name = "purchase_total", precision = 14, scale = 2)
    private BigDecimal purchaseTotal;

    @Column(name = "remaining_months")
    private Integer remainingMonths;

    @Column(name = "remaining_total", precision = 14, scale = 2)
    private BigDecimal remainingTotal;

    public StatementEntry() {
    }

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

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getMsiCurrent() {
        return msiCurrent;
    }

    public void setMsiCurrent(Integer msiCurrent) {
        this.msiCurrent = msiCurrent;
    }

    public Integer getMsiTotal() {
        return msiTotal;
    }

    public void setMsiTotal(Integer msiTotal) {
        this.msiTotal = msiTotal;
    }

    public BigDecimal getPurchaseTotal() {
        return purchaseTotal;
    }

    public void setPurchaseTotal(BigDecimal purchaseTotal) {
        this.purchaseTotal = purchaseTotal;
    }

    public Integer getRemainingMonths() {
        return remainingMonths;
    }

    public void setRemainingMonths(Integer remainingMonths) {
        this.remainingMonths = remainingMonths;
    }

    public BigDecimal getRemainingTotal() {
        return remainingTotal;
    }

    public void setRemainingTotal(BigDecimal remainingTotal) {
        this.remainingTotal = remainingTotal;
    }
}
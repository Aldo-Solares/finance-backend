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
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    @Column(name = "debtor", nullable = false)
    private String debtor;

    @Column(name = "specification")
    private String specification;

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

    @Column(name = "msi_current")
    private Integer msiCurrent;

    @Column(name = "msi_total")
    private Integer msiTotal;

    @Column(name = "purchase_amount", precision = 14, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(name = "remaining_msi")
    private Integer remainingMsi;

    @Column(name = "remaining_msi_amount", precision = 14, scale = 2)
    private BigDecimal remainingMsiAmount;

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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
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

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public Integer getRemainingMsi() {
        return remainingMsi;
    }

    public void setRemainingMsi(Integer remainingMsi) {
        this.remainingMsi = remainingMsi;
    }

    public BigDecimal getRemainingMsiAmount() {
        return remainingMsiAmount;
    }

    public void setRemainingMsiAmount(BigDecimal remainingMsiAmount) {
        this.remainingMsiAmount = remainingMsiAmount;
    }
}
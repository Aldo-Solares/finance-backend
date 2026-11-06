package com.finance.backend.modules.debts.msipurchase.model;

import com.finance.backend.modules.debts.usercard.model.UserCard;
import jakarta.persistence.*;

@Entity
@Table(name = "msi_purchases", uniqueConstraints = {
        @UniqueConstraint(name = "uk_msi_purchase_identity", columnNames = {
                "user_card_id", "purchase_key", "occurrence_number"
        })
})
public class MsiPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msi_purchase_id")
    private Long msiPurchaseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_card_id", nullable = false)
    private UserCard userCard;

    @Column(name = "purchase_key", nullable = false, length = 64)
    private String purchaseKey;

    @Column(name = "occurrence_number", nullable = false)
    private Integer occurrenceNumber;

    @Column(name = "total_monthly_payments", nullable = false)
    private Integer totalMonthlyPayments;

    public Long getMsiPurchaseId() {
        return msiPurchaseId;
    }

    public void setMsiPurchaseId(Long msiPurchaseId) {
        this.msiPurchaseId = msiPurchaseId;
    }

    public UserCard getUserCard() {
        return userCard;
    }

    public void setUserCard(UserCard userCard) {
        this.userCard = userCard;
    }

    public String getPurchaseKey() {
        return purchaseKey;
    }

    public void setPurchaseKey(String purchaseKey) {
        this.purchaseKey = purchaseKey;
    }

    public Integer getOccurrenceNumber() {
        return occurrenceNumber;
    }

    public void setOccurrenceNumber(Integer occurrenceNumber) {
        this.occurrenceNumber = occurrenceNumber;
    }

    public Integer getTotalMonthlyPayments() {
        return totalMonthlyPayments;
    }

    public void setTotalMonthlyPayments(Integer totalMonthlyPayments) {
        this.totalMonthlyPayments = totalMonthlyPayments;
    }
}

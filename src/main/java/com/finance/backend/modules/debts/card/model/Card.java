package com.finance.backend.modules.debts.card.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cards", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cards_bank_card_name", columnNames = {
                "bank",
                "card_name"
        })
})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "bank", nullable = false, length = 100)
    private String bank;

    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public Card() {
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(
            Long cardId) {
        this.cardId = cardId;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(
            String bank) {
        this.bank = bank;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(
            String cardName) {
        this.cardName = cardName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(
            Boolean active) {
        this.active = active;
    }
}
package com.finance.backend.modules.cardproduct.model;

import jakarta.persistence.*;

@Entity
@Table(name = "card_products")
public class CardProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "bank", nullable = false, length = 100)
    private String bank;

    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;

    public CardProduct() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
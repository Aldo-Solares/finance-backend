package com.finance.backend.modules.debts.usercard.model;

import java.util.ArrayList;
import java.util.List;

import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.user.model.User;

import jakarta.persistence.*;

@Entity
@Table(name = "user_cards", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_cards_user_card", columnNames = {
                "user_id",
                "card_id"
        })
})
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_card_id")
    private Long userCardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @OneToMany(mappedBy = "userCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statement> statements = new ArrayList<>();

    public UserCard() {
    }

    public Long getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(Long userCardId) {
        this.userCardId = userCardId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }
}
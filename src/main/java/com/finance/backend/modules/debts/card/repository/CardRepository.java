package com.finance.backend.modules.debts.card.repository;

import com.finance.backend.modules.debts.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository
        extends JpaRepository<Card, Long> {

    boolean existsByCardCode(String cardCode);
}
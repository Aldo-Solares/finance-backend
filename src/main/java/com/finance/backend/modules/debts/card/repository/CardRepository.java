package com.finance.backend.modules.debts.card.repository;

import com.finance.backend.modules.debts.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository
                extends JpaRepository<Card, Long> {

        List<Card> findAllByOrderByBankAscCardNameAsc();

        List<Card> findByActiveTrueOrderByBankAscCardNameAsc();

        Optional<Card> findByBankIgnoreCaseAndCardNameIgnoreCase(
                        String bank,
                        String cardName);

        boolean existsByBankIgnoreCaseAndCardNameIgnoreCase(
                        String bank,
                        String cardName);
}
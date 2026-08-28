package com.finance.backend.modules.debts.usercard.repository;

import com.finance.backend.modules.debts.usercard.model.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCardRepository
                extends JpaRepository<UserCard, Long> {

        List<UserCard> findByUserEmailIgnoreCaseOrderByUserCardIdAsc(
                        String email);

        List<UserCard> findByUserEmailIgnoreCaseAndActiveTrueOrderByUserCardIdAsc(
                        String email);

        Optional<UserCard> findByUserCardIdAndUserEmailIgnoreCase(
                        Long userCardId,
                        String email);

        boolean existsByUserEmailIgnoreCaseAndCardCardId(
                        String email,
                        Long cardId);
}
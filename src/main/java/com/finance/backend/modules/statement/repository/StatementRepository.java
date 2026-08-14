package com.finance.backend.modules.statement.repository;

import com.finance.backend.modules.statement.model.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementRepository
        extends JpaRepository<Statement, Long> {

    List<Statement> findByCardCardIdOrderByYearDescMonthDesc(
            Long cardId);

    boolean existsByCardCardIdAndYearAndMonth(
            Long cardId,
            Integer year,
            Integer month);
}
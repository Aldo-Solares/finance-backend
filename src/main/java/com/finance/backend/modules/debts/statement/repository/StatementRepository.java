// src/main/java/com/finance/backend/modules/debts/statement/repository/StatementRepository.java

package com.finance.backend.modules.debts.statement.repository;

import com.finance.backend.modules.debts.statement.model.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository
                extends JpaRepository<Statement, Long> {

        List<Statement> findByCardCardIdOrderByYearDescMonthDesc(
                        Long cardId);

        List<Statement> findByCardCardIdOrderByYearAscMonthAsc(
                        Long cardId);

        Optional<Statement> findByCardCardIdAndYearAndMonth(
                        Long cardId,
                        Integer year,
                        Integer month);

        List<Statement> findByCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                        String email);

        Optional<Statement> findByStatementIdAndCardUserEmailIgnoreCase(
                        Long statementId,
                        String email);

        List<Statement> findByCardCardIdAndCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                        Long cardId,
                        String email);

        List<Statement> findByCardCardIdAndCardUserEmailIgnoreCaseOrderByYearAscMonthAsc(
                        Long cardId,
                        String email);
}
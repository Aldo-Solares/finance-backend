package com.finance.backend.modules.debts.statement.repository;

import com.finance.backend.modules.debts.statement.model.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository
                extends JpaRepository<Statement, Long> {

        List<Statement> findByUserCardUserCardIdOrderByYearDescMonthDesc(
                        Long userCardId);

        boolean existsByUserCardUserCardIdAndYearAndMonth(
                        Long userCardId,
                        Integer year,
                        Integer month);

        List<Statement> findByUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                        String email);

        Optional<Statement> findByStatementIdAndUserCardUserEmailIgnoreCase(
                        Long statementId,
                        String email);

        List<Statement> findByUserCardUserCardIdAndUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                        Long userCardId,
                        String email);

        Optional<Statement> findByUserCardUserCardIdAndYearAndMonthAndUserCardUserEmailIgnoreCase(
                        Long userCardId,
                        Integer year,
                        Integer month,
                        String email);

        Optional<Statement> findFirstByUserCardUserCardIdAndUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                        Long userCardId,
                        String email);
}
package com.finance.backend.modules.debts.statemententry.repository;

import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.model.StatementEntrySource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementEntryRepository
                extends JpaRepository<StatementEntry, Long> {

        List<StatementEntry> findByStatementStatementId(
                        Long statementId);

        List<StatementEntry> findByDebtor(
                        String debtor);

        List<StatementEntry> findByStatementStatementIdAndDebtor(
                        Long statementId,
                        String debtor);

        List<StatementEntry> findByStatementStatementIdAndSource(
                        Long statementId,
                        StatementEntrySource source);

        List<StatementEntry> findByStatementCardCardIdAndSource(
                        Long cardId,
                        StatementEntrySource source);

        Optional<StatementEntry> findByEntryIdAndStatementCardUserEmailIgnoreCase(
                        Long entryId,
                        String email);

        List<StatementEntry> findByStatementStatementIdAndStatementCardUserEmailIgnoreCase(
                        Long statementId,
                        String email);

        List<StatementEntry> findByDebtorAndStatementCardUserEmailIgnoreCase(
                        String debtor,
                        String email);

        List<StatementEntry> findByStatementStatementIdAndDebtorAndStatementCardUserEmailIgnoreCase(
                        Long statementId,
                        String debtor,
                        String email);

        List<StatementEntry> findByStatementCardUserEmailIgnoreCase(
                        String email);
}
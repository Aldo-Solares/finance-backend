package com.finance.backend.modules.debts.statemententry.repository;

import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StatementEntryRepository
                extends JpaRepository<StatementEntry, Long>,
                JpaSpecificationExecutor<StatementEntry> {

        List<StatementEntry> findByDebtor(
                        String debtor);

        boolean existsByStatementStatementId(
                        Long statementId);

        List<StatementEntry> findByStatementStatementIdAndDebtor(
                        Long statementId,
                        String debtor);

        Optional<StatementEntry> findByEntryIdAndStatementUserCardUserEmailIgnoreCase(
                        Long entryId,
                        String email);

        List<StatementEntry> findByStatementStatementIdAndStatementUserCardUserEmailIgnoreCaseOrderByDateDesc(
                        Long statementId,
                        String email);

        List<StatementEntry> findByDebtorAndStatementUserCardUserEmailIgnoreCase(
                        String debtor,
                        String email);

        List<StatementEntry> findByStatementStatementIdAndDebtorAndStatementUserCardUserEmailIgnoreCase(
                        Long statementId,
                        String debtor,
                        String email);

        List<StatementEntry> findByStatementUserCardUserEmailIgnoreCase(
                        String email);
}
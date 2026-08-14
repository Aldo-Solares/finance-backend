package com.finance.backend.modules.statemententry.repository;

import com.finance.backend.modules.statemententry.model.StatementEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementEntryRepository
                extends JpaRepository<StatementEntry, Long> {

        List<StatementEntry> findByStatementStatementId(
                        Long statementId);

        List<StatementEntry> findByUserUserId(
                        Long userId);

        List<StatementEntry> findByStatementStatementIdAndUserUserId(
                        Long statementId,
                        Long userId);
}
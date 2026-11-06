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

        long countByStatementStatementId(
                        Long statementId);

        long countByStatementStatementIdAndPaidFalse(
                        Long statementId);

        List<StatementEntry> findByStatementStatementId(
                        Long statementId);

        List<StatementEntry> findByStatementStatementIdIn(
                        List<Long> statementIds);

        boolean existsByCategoryCategoryId(
                        Long categoryId);

        Optional<StatementEntry> findByImportKey(
                        String importKey);

        Optional<StatementEntry> findFirstByMsiPurchaseMsiPurchaseIdAndStatementStatementIdAndMonthlyPaymentNumber(
                        Long msiPurchaseId,
                        Long statementId,
                        Integer monthlyPaymentNumber);

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

        List<StatementEntry> findByMerchantIgnoreCase(
                        String merchant);

        List<StatementEntry> findByMerchantIgnoreCaseAndStatementUserCardUserEmailIgnoreCase(
                        String merchant,
                        String email);
}

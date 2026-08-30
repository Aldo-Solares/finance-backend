// src/main/java/com/finance/backend/modules/debts/statemententry/service/StatementEntryService.java

package com.finance.backend.modules.debts.statemententry.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.concept.repository.ConceptRepository;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.mapper.StatementEntryMapper;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class StatementEntryService {

        private final StatementEntryRepository statementEntryRepository;
        private final StatementRepository statementRepository;
        private final ConceptRepository conceptRepository;

        public StatementEntryService(
                        StatementEntryRepository statementEntryRepository,
                        StatementRepository statementRepository,
                        ConceptRepository conceptRepository) {

                this.statementEntryRepository = statementEntryRepository;
                this.statementRepository = statementRepository;
                this.conceptRepository = conceptRepository;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findAll(
                        String email) {

                return statementEntryRepository
                                .findByStatementUserCardUserEmailIgnoreCase(
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public StatementEntryResponse findById(
                        Long entryId,
                        String email) {

                return StatementEntryMapper.toResponse(
                                getOwnedEntry(
                                                entryId,
                                                email));
        }

        // ===================
        // FIND BY STATEMENT
        // ===================

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByStatementId(
                        Long statementId,
                        String email) {

                getOwnedStatement(
                                statementId,
                                email);

                return statementEntryRepository
                                .findByStatementStatementIdAndStatementUserCardUserEmailIgnoreCaseOrderByDateDesc(
                                                statementId,
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY DEBTOR
        // ===================

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByDebtor(
                        String debtor,
                        String email) {

                return statementEntryRepository
                                .findByDebtorAndStatementUserCardUserEmailIgnoreCase(
                                                debtor,
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY STATEMENT AND DEBTOR
        // ===================

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByStatementIdAndDebtor(
                        Long statementId,
                        String debtor,
                        String email) {

                getOwnedStatement(
                                statementId,
                                email);

                return statementEntryRepository
                                .findByStatementStatementIdAndDebtorAndStatementUserCardUserEmailIgnoreCase(
                                                statementId,
                                                debtor,
                                                email)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        // ===================
        // CREATE
        // ===================

        public StatementEntryResponse create(
                        CreateStatementEntryRequest request,
                        String email) {

                validateEntryType(
                                request.entryType(),
                                request.msiCurrent(),
                                request.msiTotal());

                Statement statement = getOwnedStatement(
                                request.statementId(),
                                email);

                Concept concept = getConcept(
                                request.conceptId());

                StatementEntry entry = StatementEntryMapper.toEntity(
                                request,
                                statement,
                                concept);

                calculateMsiValues(
                                entry);

                StatementEntry savedEntry = statementEntryRepository.save(
                                entry);

                return StatementEntryMapper.toResponse(
                                savedEntry);
        }

        // ===================
        // UPDATE
        // ===================

        public StatementEntryResponse update(
                        Long entryId,
                        UpdateStatementEntryRequest request,
                        String email) {

                validateEntryType(
                                request.entryType(),
                                request.msiCurrent(),
                                request.msiTotal());

                StatementEntry entry = getOwnedEntry(
                                entryId,
                                email);

                Statement statement = getOwnedStatement(
                                request.statementId(),
                                email);

                Concept concept = getConcept(
                                request.conceptId());

                StatementEntryMapper.updateEntity(
                                entry,
                                request,
                                statement,
                                concept);

                calculateMsiValues(
                                entry);

                StatementEntry updatedEntry = statementEntryRepository.save(
                                entry);

                return StatementEntryMapper.toResponse(
                                updatedEntry);
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long entryId,
                        String email) {

                StatementEntry entry = getOwnedEntry(
                                entryId,
                                email);

                statementEntryRepository.delete(
                                entry);
        }

        // ===================
        // VALIDATE ENTRY TYPE
        // ===================

        private void validateEntryType(
                        StatementEntryType entryType,
                        Integer msiCurrent,
                        Integer msiTotal) {

                if (entryType != StatementEntryType.RECURRING) {
                        return;
                }

                if (msiCurrent != null
                                || msiTotal != null) {

                        throw new IllegalArgumentException(
                                        "Los movimientos recurrentes no pueden tener información MSI");
                }
        }

        // ===================
        // CALCULATE MSI
        // ===================

        private void calculateMsiValues(
                        StatementEntry entry) {

                if (entry.getEntryType() != StatementEntryType.PURCHASE
                                || entry.getMsiCurrent() == null
                                || entry.getMsiTotal() == null) {

                        entry.setPurchaseAmount(null);
                        entry.setRemainingMsi(null);
                        entry.setRemainingMsiAmount(null);

                        return;
                }

                int msiCurrent = entry.getMsiCurrent();
                int msiTotal = entry.getMsiTotal();

                if (msiCurrent < 1
                                || msiTotal < 1
                                || msiCurrent > msiTotal) {

                        throw new IllegalArgumentException(
                                        "La mensualidad MSI debe estar entre 1 y el total de meses");
                }

                BigDecimal purchaseAmount = entry.getAmount()
                                .multiply(
                                                BigDecimal.valueOf(msiTotal))
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                int remainingMsi = msiTotal - msiCurrent + 1;

                BigDecimal remainingMsiAmount = entry.getAmount()
                                .multiply(
                                                BigDecimal.valueOf(remainingMsi))
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                entry.setPurchaseAmount(
                                purchaseAmount);

                entry.setRemainingMsi(
                                remainingMsi);

                entry.setRemainingMsiAmount(
                                remainingMsiAmount);
        }

        // ===================
        // OWNED ENTRY
        // ===================

        private StatementEntry getOwnedEntry(
                        Long entryId,
                        String email) {

                return statementEntryRepository
                                .findByEntryIdAndStatementUserCardUserEmailIgnoreCase(
                                                entryId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Movimiento no encontrado"));
        }

        // ===================
        // OWNED STATEMENT
        // ===================

        private Statement getOwnedStatement(
                        Long statementId,
                        String email) {

                return statementRepository
                                .findByStatementIdAndUserCardUserEmailIgnoreCase(
                                                statementId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Estado de cuenta no encontrado"));
        }

        // ===================
        // CONCEPT
        // ===================

        private Concept getConcept(
                        Long conceptId) {

                return conceptRepository
                                .findById(
                                                conceptId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Concepto no encontrado"));
        }
}
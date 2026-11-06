package com.finance.backend.modules.debts.statemententry.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.concept.repository.ConceptRepository;
import com.finance.backend.modules.debts.msipurchase.model.MsiPurchase;
import com.finance.backend.modules.debts.msipurchase.service.MsiPurchaseService;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;
import com.finance.backend.modules.debts.statement.service.StatementService;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.mapper.StatementEntryMapper;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class StatementEntryService {

        private final StatementEntryRepository statementEntryRepository;
        private final StatementRepository statementRepository;
        private final StatementService statementService;
        private final ConceptRepository conceptRepository;
        private final MsiPurchaseService msiPurchaseService;
        private final StatementEntryPolicy statementEntryPolicy;

        public StatementEntryService(
                        StatementEntryRepository statementEntryRepository,
                        StatementRepository statementRepository,
                        StatementService statementService,
                        ConceptRepository conceptRepository,
                        MsiPurchaseService msiPurchaseService,
                        StatementEntryPolicy statementEntryPolicy) {

                this.statementEntryRepository = statementEntryRepository;
                this.statementRepository = statementRepository;
                this.statementService = statementService;
                this.conceptRepository = conceptRepository;
                this.msiPurchaseService = msiPurchaseService;
                this.statementEntryPolicy = statementEntryPolicy;
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

                Statement statement = getOwnedStatement(
                                request.statementId(),
                                email);

                Concept concept = getCategory(
                                request.categoryId());
                String merchant = resolveMerchant(request.merchant());
                StatementEntryType entryType = statementEntryPolicy.classify(concept, merchant);

                validateEntryType(
                                entryType,
                                request.monthlyPaymentNumber(),
                                request.totalMonthlyPayments());

                StatementEntry entry = StatementEntryMapper.toEntity(
                                request,
                                statement,
                                concept);

                entry.setMerchant(merchant);
                entry.setEntryType(entryType);
                entry.setDate(statementEntryPolicy.resolveDate(
                                request.date(), statement, merchant, entryType));

                entry.setMsiPurchase(resolveMsiPurchase(
                                entry,
                                concept,
                                request.totalMonthlyPayments()));

                StatementEntry savedEntry = statementEntryRepository.save(
                                entry);

                statementService.refreshPaymentStatus(statement.getStatementId());

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

                StatementEntry entry = getOwnedEntry(
                                entryId,
                                email);
                Long previousStatementId = entry.getStatement().getStatementId();

                Statement statement = getOwnedStatement(
                                request.statementId(),
                                email);

                Concept concept = getCategory(
                                request.categoryId());
                String merchant = resolveMerchant(request.merchant());
                StatementEntryType entryType = statementEntryPolicy.classify(concept, merchant);

                validateEntryType(
                                entryType,
                                request.monthlyPaymentNumber(),
                                request.totalMonthlyPayments());

                java.time.LocalDate resolvedDate = statementEntryPolicy.resolveDate(
                                request.date(), statement, merchant, entryType);
                boolean existingIsMsi = entry.getMsiPurchase() != null
                                || entry.getMonthlyPaymentNumber() != null;
                if (existingIsMsi
                                && !Objects.equals(entry.getDate(), resolvedDate)) {
                        throw new BadRequestException(
                                        "La fecha de una compra MSI es fija; conserva la fecha original de compra");
                }

                StatementEntryMapper.updateEntity(
                                entry,
                                request,
                                statement,
                                concept);

                entry.setMerchant(merchant);
                entry.setEntryType(entryType);
                entry.setDate(resolvedDate);

                entry.setMsiPurchase(resolveMsiPurchase(
                                entry,
                                concept,
                                request.totalMonthlyPayments()));

                StatementEntry updatedEntry = statementEntryRepository.save(
                                entry);

                statementService.refreshPaymentStatus(previousStatementId);
                if (!previousStatementId.equals(statement.getStatementId())) {
                        statementService.refreshPaymentStatus(statement.getStatementId());
                }

                return StatementEntryMapper.toResponse(
                                updatedEntry);
        }

        // ===================
        // PAY SELECTED
        // ===================

        public List<StatementEntryResponse> paySelected(
                        List<Long> entryIds,
                        String email) {

                if (entryIds == null || entryIds.isEmpty()) {
                        throw new BadRequestException(
                                        "Selecciona al menos un movimiento");
                }

                if (entryIds.stream().anyMatch(entryId -> entryId == null || entryId <= 0)) {
                        throw new BadRequestException(
                                        "Los movimientos seleccionados no son válidos");
                }

                List<StatementEntry> entries = new ArrayList<>();
                LinkedHashSet<Long> affectedStatementIds = new LinkedHashSet<>();
                for (Long entryId : new LinkedHashSet<>(entryIds)) {
                        StatementEntry entry = getOwnedEntry(entryId, email);
                        entry.setPaid(true);
                        affectedStatementIds.add(entry.getStatement().getStatementId());
                        entries.add(entry);
                }

                List<StatementEntry> savedEntries = statementEntryRepository.saveAll(entries);
                affectedStatementIds.forEach(statementService::refreshPaymentStatus);
                return savedEntries
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
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
                Long statementId = entry.getStatement().getStatementId();

                statementEntryRepository.delete(
                                entry);
                statementService.refreshPaymentStatus(statementId);
        }

        // ===================
        // RESOLVE MERCHANT
        // ===================

        private String resolveMerchant(String requestedMerchant) {

                if (requestedMerchant != null && !requestedMerchant.isBlank()) {
                        return requestedMerchant.trim();
                }
                return null;
        }

        // ===================
        // VALIDATE ENTRY TYPE
        // ===================

        private void validateEntryType(
                        StatementEntryType entryType,
                        Integer monthlyPaymentNumber,
                        Integer totalMonthlyPayments) {

                if (entryType != StatementEntryType.RECURRING) {
                        if ((monthlyPaymentNumber == null) != (totalMonthlyPayments == null)) {
                                throw new IllegalArgumentException(
                                                "Indica tanto la mensualidad actual como el total de meses MSI");
                        }
                        return;
                }

                if (monthlyPaymentNumber != null
                                || totalMonthlyPayments != null) {

                        throw new IllegalArgumentException(
                                        "Los movimientos recurrentes no pueden tener información MSI");
                }
        }

        private MsiPurchase resolveMsiPurchase(
                        StatementEntry entry,
                        Concept category,
                        Integer totalMonthlyPayments) {

                return msiPurchaseService.resolveOrCreate(
                                entry.getStatement().getUserCard(),
                                entry.getStatement(),
                                category.getCategoryId(),
                                entry.getDebtor(),
                                entry.getDescription(),
                                entry.getMerchant(),
                                entry.getDate(),
                                entry.getMonthlyPaymentNumber(),
                                entry.getEntryType() == StatementEntryType.RECURRING
                                                ? null
                                                : totalMonthlyPayments,
                                entry);
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

        private Concept getCategory(
                        Long categoryId) {

                return conceptRepository
                                .findById(
                                                categoryId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Categoría no encontrada"));
        }
}

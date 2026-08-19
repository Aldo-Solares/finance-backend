package com.finance.backend.modules.debts.statemententry.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.concept.repository.ConceptRepository;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;
import com.finance.backend.modules.debts.statement.service.StatementService;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.mapper.StatementEntryMapper;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.model.StatementEntrySource;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class StatementEntryService {

        private final StatementEntryRepository statementEntryRepository;
        private final StatementRepository statementRepository;
        private final ConceptRepository conceptRepository;
        private final StatementService statementService;

        public StatementEntryService(
                        StatementEntryRepository statementEntryRepository,
                        StatementRepository statementRepository,
                        ConceptRepository conceptRepository,
                        StatementService statementService) {
                this.statementEntryRepository = statementEntryRepository;
                this.statementRepository = statementRepository;
                this.conceptRepository = conceptRepository;
                this.statementService = statementService;
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findAll() {
                return statementEntryRepository
                                .findAll()
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public StatementEntryResponse findById(
                        Long entryId) {
                return StatementEntryMapper.toResponse(
                                getEntry(entryId));
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByStatementId(
                        Long statementId) {
                getStatement(statementId);

                return statementEntryRepository
                                .findByStatementStatementId(statementId)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByDebtor(
                        String debtor) {
                return statementEntryRepository
                                .findByDebtor(debtor)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<StatementEntryResponse> findByStatementIdAndDebtor(
                        Long statementId,
                        String debtor) {
                getStatement(statementId);

                return statementEntryRepository
                                .findByStatementStatementIdAndDebtor(
                                                statementId,
                                                debtor)
                                .stream()
                                .map(StatementEntryMapper::toResponse)
                                .toList();
        }

        @Transactional
        public StatementEntryResponse create(
                        CreateStatementEntryRequest request) {

                Statement statement = getStatement(
                                request.statementId());

                Concept concept = getConcept(
                                request.conceptId());

                StatementEntry entry = findMatchingProjectedEntry(
                                request,
                                statement,
                                concept);

                if (entry == null) {
                        entry = StatementEntryMapper.toEntity(
                                        request,
                                        statement,
                                        concept);
                } else {
                        StatementEntryMapper.updateEntity(
                                        entry,
                                        request,
                                        statement,
                                        concept);
                }

                StatementEntry savedEntry = statementEntryRepository.save(
                                entry);

                rebuildMsiProjection(
                                savedEntry);

                return StatementEntryMapper.toResponse(
                                savedEntry);
        }

        @Transactional
        public StatementEntryResponse update(
                        Long entryId,
                        UpdateStatementEntryRequest request) {

                StatementEntry entry = getEntry(
                                entryId);

                Statement statement = getStatement(
                                request.statementId());

                Concept concept = getConcept(
                                request.conceptId());

                StatementEntryMapper.updateEntity(
                                entry,
                                request,
                                statement,
                                concept);

                StatementEntry updatedEntry = statementEntryRepository.save(
                                entry);

                if (updatedEntry.getSource() == StatementEntrySource.ACTUAL) {
                        rebuildMsiProjection(
                                        updatedEntry);
                }

                return StatementEntryMapper.toResponse(
                                updatedEntry);
        }

        public void delete(
                        Long entryId) {
                StatementEntry entry = getEntry(
                                entryId);

                statementEntryRepository.delete(
                                entry);
        }

        private void rebuildMsiProjection(
                        StatementEntry actualEntry) {

                Integer current = actualEntry.getMsiCurrent();
                Integer total = actualEntry.getMsiTotal();

                if (current == null || total == null) {
                        return;
                }

                if (current < 1 || total < 1 || current > total) {
                        throw new IllegalStateException(
                                        "MSI inválido: msiCurrent debe estar entre 1 y msiTotal");
                }

                deleteFutureProjectedEntries(
                                actualEntry);

                int futureMonths = total - current;

                if (futureMonths == 0) {
                        return;
                }

                List<Statement> futureStatements = statementService
                                .ensureProjectedStatements(
                                                actualEntry.getStatement(),
                                                futureMonths);

                List<StatementEntry> projectedEntries = new ArrayList<>();

                for (int offset = 1; offset <= futureMonths; offset++) {

                        Integer projectedCurrent = current + offset;

                        Integer remainingMonths = total - projectedCurrent;

                        StatementEntry projected = new StatementEntry();

                        projected.setStatement(
                                        futureStatements.get(offset - 1));

                        projected.setConcept(
                                        actualEntry.getConcept());

                        projected.setDebtor(
                                        actualEntry.getDebtor());

                        projected.setDescription(
                                        actualEntry.getDescription());

                        projected.setPurchaseDate(
                                        actualEntry.getPurchaseDate());

                        projected.setInstallmentAmount(
                                        actualEntry.getInstallmentAmount());

                        projected.setPaid(false);

                        projected.setMsiCurrent(
                                        projectedCurrent);

                        projected.setMsiTotal(
                                        total);

                        projected.setPurchaseTotal(
                                        actualEntry.getPurchaseTotal());

                        projected.setRemainingMonths(
                                        remainingMonths);

                        projected.setRemainingTotal(
                                        calculateRemainingTotal(
                                                        actualEntry.getInstallmentAmount(),
                                                        remainingMonths));

                        projected.setSource(
                                        StatementEntrySource.PROJECTED);

                        projectedEntries.add(
                                        projected);
                }

                statementEntryRepository.saveAll(
                                projectedEntries);
        }

        private void deleteFutureProjectedEntries(
                        StatementEntry actualEntry) {

                Long cardId = actualEntry
                                .getStatement()
                                .getCard()
                                .getCardId();

                YearMonth actualMonth = YearMonth.of(
                                actualEntry.getStatement().getYear(),
                                actualEntry.getStatement().getMonth());

                List<StatementEntry> projectedEntries = statementEntryRepository
                                .findByStatementCardCardIdAndSource(
                                                cardId,
                                                StatementEntrySource.PROJECTED);

                List<StatementEntry> entriesToDelete = projectedEntries
                                .stream()
                                .filter(entry -> isSameMsiSeries(
                                                entry,
                                                actualEntry))
                                .filter(entry -> {
                                        YearMonth entryMonth = YearMonth.of(
                                                        entry.getStatement().getYear(),
                                                        entry.getStatement().getMonth());

                                        return entryMonth.isAfter(
                                                        actualMonth);
                                })
                                .toList();

                if (!entriesToDelete.isEmpty()) {
                        statementEntryRepository.deleteAll(
                                        entriesToDelete);
                }
        }

        private StatementEntry findMatchingProjectedEntry(
                        CreateStatementEntryRequest request,
                        Statement statement,
                        Concept concept) {

                if (request.msiCurrent() == null
                                || request.msiTotal() == null) {
                        return null;
                }

                List<StatementEntry> candidates = statementEntryRepository
                                .findByStatementStatementIdAndSource(
                                                statement.getStatementId(),
                                                StatementEntrySource.PROJECTED)
                                .stream()
                                .filter(entry -> isSameMsiSeries(
                                                entry,
                                                request,
                                                concept))
                                .filter(entry -> Objects.equals(
                                                entry.getMsiCurrent(),
                                                request.msiCurrent()))
                                .toList();

                if (candidates.size() > 1) {
                        throw new IllegalStateException(
                                        "Se encontraron múltiples movimientos MSI proyectados para el mismo movimiento real");
                }

                if (candidates.isEmpty()) {
                        return null;
                }

                return candidates.get(0);
        }

        private boolean isSameMsiSeries(
                        StatementEntry left,
                        StatementEntry right) {

                return Objects.equals(
                                left.getConcept().getConceptId(),
                                right.getConcept().getConceptId())
                                && Objects.equals(
                                                left.getDebtor(),
                                                right.getDebtor())
                                && Objects.equals(
                                                left.getDescription(),
                                                right.getDescription())
                                && Objects.equals(
                                                left.getPurchaseDate(),
                                                right.getPurchaseDate())
                                && Objects.equals(
                                                left.getMsiTotal(),
                                                right.getMsiTotal())
                                && decimalEquals(
                                                left.getPurchaseTotal(),
                                                right.getPurchaseTotal());
        }

        private boolean isSameMsiSeries(
                        StatementEntry entry,
                        CreateStatementEntryRequest request,
                        Concept concept) {

                return Objects.equals(
                                entry.getConcept().getConceptId(),
                                concept.getConceptId())
                                && Objects.equals(
                                                entry.getDebtor(),
                                                request.debtor())
                                && Objects.equals(
                                                entry.getDescription(),
                                                request.description())
                                && Objects.equals(
                                                entry.getPurchaseDate(),
                                                request.purchaseDate())
                                && Objects.equals(
                                                entry.getMsiTotal(),
                                                request.msiTotal())
                                && decimalEquals(
                                                entry.getPurchaseTotal(),
                                                request.purchaseTotal());
        }

        private boolean decimalEquals(
                        BigDecimal left,
                        BigDecimal right) {

                if (left == null && right == null) {
                        return true;
                }

                if (left == null || right == null) {
                        return false;
                }

                return left.compareTo(right) == 0;
        }

        private BigDecimal calculateRemainingTotal(
                        BigDecimal installmentAmount,
                        Integer remainingMonths) {

                if (installmentAmount == null) {
                        return null;
                }

                return installmentAmount.multiply(
                                BigDecimal.valueOf(
                                                remainingMonths));
        }

        private StatementEntry getEntry(
                        Long entryId) {
                return statementEntryRepository
                                .findById(entryId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Movimiento no encontrado"));
        }

        private Statement getStatement(
                        Long statementId) {
                return statementRepository
                                .findById(statementId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Estado de cuenta no encontrado"));
        }

        private Concept getConcept(
                        Long conceptId) {
                return conceptRepository
                                .findById(conceptId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Concepto no encontrado"));
        }
}
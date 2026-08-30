package com.finance.backend.modules.debts.statement.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.StatementDateSuggestionResponse;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.mapper.StatementMapper;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.model.StatementStatus;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import com.finance.backend.modules.debts.usercard.model.UserCard;
import com.finance.backend.modules.debts.usercard.repository.UserCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StatementService {

        private final StatementRepository statementRepository;
        private final StatementEntryRepository statementEntryRepository;
        private final UserCardRepository userCardRepository;

        public StatementService(
                        StatementRepository statementRepository,
                        StatementEntryRepository statementEntryRepository,
                        UserCardRepository userCardRepository) {

                this.statementRepository = statementRepository;
                this.statementEntryRepository = statementEntryRepository;
                this.userCardRepository = userCardRepository;
        }

        // ===================
        // FIND ALL
        // ===================

        @Transactional(readOnly = true)
        public List<StatementResponse> findAll(
                        String email) {

                return statementRepository
                                .findByUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                email)
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY USER CARD
        // ===================

        @Transactional(readOnly = true)
        public List<StatementResponse> findByUserCardId(
                        Long userCardId,
                        String email) {

                getOwnedUserCard(
                                userCardId,
                                email);

                return statementRepository
                                .findByUserCardUserCardIdAndUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                userCardId,
                                                email)
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        @Transactional(readOnly = true)
        public StatementResponse findById(
                        Long statementId,
                        String email) {

                return StatementMapper.toResponse(
                                getOwnedStatement(
                                                statementId,
                                                email));
        }

        // ===================
        // DATE SUGGESTION
        // ===================

        @Transactional(readOnly = true)
        public StatementDateSuggestionResponse getDateSuggestion(
                        Long userCardId,
                        String email) {

                getOwnedUserCard(
                                userCardId,
                                email);

                Statement latestStatement = statementRepository
                                .findFirstByUserCardUserCardIdAndUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                userCardId,
                                                email)
                                .orElse(null);

                if (latestStatement == null) {
                        return new StatementDateSuggestionResponse(
                                        null,
                                        null,
                                        null);
                }

                return new StatementDateSuggestionResponse(
                                plusMonth(
                                                latestStatement.getPeriodStart()),
                                plusMonth(
                                                latestStatement.getPeriodEnd()),
                                plusMonth(
                                                latestStatement.getPaymentDate()));
        }

        // ===================
        // CREATE
        // ===================

        public StatementResponse create(
                        CreateStatementRequest request,
                        String email) {

                UserCard userCard = getOwnedUserCard(
                                request.userCardId(),
                                email);

                int year = request
                                .periodEnd()
                                .getYear();

                int month = request
                                .periodEnd()
                                .getMonthValue();

                boolean exists = statementRepository
                                .existsByUserCardUserCardIdAndYearAndMonth(
                                                request.userCardId(),
                                                year,
                                                month);

                if (exists) {
                        throw new ConflictException(
                                        "Ya existe un estado de cuenta para "
                                                        + year
                                                        + "-"
                                                        + month);
                }

                Statement previousStatement = findPreviousStatement(
                                request.userCardId(),
                                year,
                                month,
                                email);

                Statement statement = StatementMapper.toEntity(
                                request,
                                userCard);

                statement.setPaid(
                                false);

                statement.setStatus(
                                calculateStatus(
                                                statement,
                                                LocalDate.now()));

                Statement savedStatement = statementRepository.save(
                                statement);

                if (previousStatement != null) {
                        createNextMsiEntries(
                                        previousStatement,
                                        savedStatement,
                                        email);
                }

                return StatementMapper.toResponse(
                                savedStatement);
        }

        // ===================
        // UPDATE
        // ===================

        public StatementResponse update(
                        Long statementId,
                        UpdateStatementRequest request,
                        String email) {

                Statement statement = getOwnedStatement(
                                statementId,
                                email);

                UserCard userCard = getOwnedUserCard(
                                request.userCardId(),
                                email);

                int year = request
                                .periodEnd()
                                .getYear();

                int month = request
                                .periodEnd()
                                .getMonthValue();

                boolean periodChanged = !statement.getUserCard()
                                .getUserCardId()
                                .equals(request.userCardId())
                                || !statement.getYear()
                                                .equals(year)
                                || !statement.getMonth()
                                                .equals(month);

                if (periodChanged) {

                        boolean exists = statementRepository
                                        .existsByUserCardUserCardIdAndYearAndMonth(
                                                        request.userCardId(),
                                                        year,
                                                        month);

                        if (exists) {
                                throw new ConflictException(
                                                "Ya existe un estado de cuenta para "
                                                                + year
                                                                + "-"
                                                                + month);
                        }
                }

                StatementMapper.updateEntity(
                                statement,
                                request,
                                userCard);

                statement.setStatus(
                                calculateStatus(
                                                statement,
                                                LocalDate.now()));

                Statement updatedStatement = statementRepository.save(
                                statement);

                return StatementMapper.toResponse(
                                updatedStatement);
        }

        // ===================
        // PAID
        // ===================

        public StatementResponse updatePaid(
                        Long statementId,
                        Boolean paid,
                        String email) {

                Statement statement = getOwnedStatement(
                                statementId,
                                email);

                statement.setPaid(
                                paid);

                Statement updatedStatement = statementRepository.save(
                                statement);

                return StatementMapper.toResponse(
                                updatedStatement);
        }

        // ===================
        // PAY ALL
        // ===================

        public List<StatementResponse> payAll(
                        Long userCardId,
                        String email) {

                getOwnedUserCard(
                                userCardId,
                                email);

                List<Statement> statements = statementRepository
                                .findByUserCardUserCardIdAndUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                userCardId,
                                                email);

                List<Statement> statementsToUpdate = new ArrayList<>();

                for (Statement statement : statements) {

                        if (!Boolean.TRUE.equals(
                                        statement.getPaid())) {

                                statement.setPaid(
                                                true);

                                statementsToUpdate.add(
                                                statement);
                        }
                }

                if (!statementsToUpdate.isEmpty()) {
                        statementRepository.saveAll(
                                        statementsToUpdate);
                }

                return statements
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        // ===================
        // CREATE NEXT MSI
        // ===================

        public void createNextMsiEntries(
                        Statement previousStatement,
                        Statement newStatement,
                        String email) {

                List<StatementEntry> previousEntries = statementEntryRepository
                                .findByStatementStatementIdAndStatementUserCardUserEmailIgnoreCaseOrderByDateDesc(
                                                previousStatement.getStatementId(),
                                                email);

                for (StatementEntry previousEntry : previousEntries) {

                        if (!hasActiveMsi(
                                        previousEntry)) {

                                continue;
                        }

                        int nextMsiCurrent = previousEntry.getMsiCurrent() + 1;

                        int remainingMsi = previousEntry.getMsiTotal()
                                        - nextMsiCurrent
                                        + 1;

                        BigDecimal remainingMsiAmount = previousEntry
                                        .getAmount()
                                        .multiply(
                                                        BigDecimal.valueOf(
                                                                        remainingMsi));

                        StatementEntry newEntry = new StatementEntry();

                        newEntry.setStatement(
                                        newStatement);

                        newEntry.setConcept(
                                        previousEntry.getConcept());

                        newEntry.setDebtor(
                                        previousEntry.getDebtor());

                        newEntry.setSpecification(
                                        previousEntry.getSpecification());

                        newEntry.setNotes(
                                        previousEntry.getNotes());

                        newEntry.setEntryType(
                                        previousEntry.getEntryType());

                        newEntry.setDate(
                                        previousEntry.getDate());

                        newEntry.setAmount(
                                        previousEntry.getAmount());

                        newEntry.setPaid(
                                        false);

                        newEntry.setMsiCurrent(
                                        nextMsiCurrent);

                        newEntry.setMsiTotal(
                                        previousEntry.getMsiTotal());

                        newEntry.setPurchaseAmount(
                                        previousEntry.getPurchaseAmount());

                        newEntry.setRemainingMsi(
                                        remainingMsi);

                        newEntry.setRemainingMsiAmount(
                                        remainingMsiAmount);

                        statementEntryRepository.save(
                                        newEntry);
                }
        }

        private boolean hasActiveMsi(
                        StatementEntry entry) {

                return entry.getEntryType() == StatementEntryType.PURCHASE
                                && entry.getMsiCurrent() != null
                                && entry.getMsiTotal() != null
                                && entry.getMsiCurrent() < entry.getMsiTotal();
        }

        // ===================
        // REFRESH STATUS
        // ===================

        public void refreshStatuses() {

                LocalDate today = LocalDate.now();

                List<Statement> statements = statementRepository.findAll();

                List<Statement> changedStatements = new ArrayList<>();

                for (Statement statement : statements) {

                        StatementStatus newStatus = calculateStatus(
                                        statement,
                                        today);

                        if (statement.getStatus() != newStatus) {

                                statement.setStatus(
                                                newStatus);

                                changedStatements.add(
                                                statement);
                        }
                }

                if (!changedStatements.isEmpty()) {
                        statementRepository.saveAll(
                                        changedStatements);
                }
        }

        // ===================
        // DELETE
        // ===================

        public void delete(
                        Long statementId,
                        String email) {

                Statement statement = getOwnedStatement(
                                statementId,
                                email);

                boolean hasEntries = statementEntryRepository
                                .existsByStatementStatementId(
                                                statementId);

                if (hasEntries) {
                        throw new ConflictException(
                                        "No se puede eliminar el estado de cuenta porque tiene movimientos registrados");
                }

                statementRepository.delete(
                                statement);
        }

        // ===================
        // STATUS
        // ===================

        private StatementStatus calculateStatus(
                        Statement statement,
                        LocalDate today) {

                LocalDate periodStart = statement.getPeriodStart();

                LocalDate periodEnd = statement.getPeriodEnd();

                LocalDate paymentDate = statement.getPaymentDate();

                if (periodStart == null
                                || periodEnd == null
                                || paymentDate == null) {

                        throw new IllegalStateException(
                                        "No se puede calcular el estado del periodo sin periodStart, periodEnd y paymentDate");
                }

                if (today.isBefore(
                                periodStart)) {

                        return StatementStatus.UPCOMING;
                }

                if (!today.isAfter(
                                periodEnd)) {

                        return StatementStatus.ACTIVE;
                }

                if (!today.isAfter(
                                paymentDate)) {

                        return StatementStatus.PAYMENT_PENDING;
                }

                return StatementStatus.CLOSED;
        }

        // ===================
        // PREVIOUS STATEMENT
        // ===================

        private Statement findPreviousStatement(
                        Long userCardId,
                        Integer year,
                        Integer month,
                        String email) {

                int previousYear = year;
                int previousMonth = month - 1;

                if (previousMonth == 0) {
                        previousMonth = 12;
                        previousYear--;
                }

                return statementRepository
                                .findByUserCardUserCardIdAndYearAndMonthAndUserCardUserEmailIgnoreCase(
                                                userCardId,
                                                previousYear,
                                                previousMonth,
                                                email)
                                .orElse(null);
        }

        // ===================
        // PLUS MONTH
        // ===================

        private LocalDate plusMonth(
                        LocalDate date) {

                if (date == null) {
                        return null;
                }

                return date.plusMonths(1);
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
        // OWNED USER CARD
        // ===================

        private UserCard getOwnedUserCard(
                        Long userCardId,
                        String email) {

                return userCardRepository
                                .findByUserCardIdAndUserEmailIgnoreCase(
                                                userCardId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta del usuario no encontrada"));
        }
}
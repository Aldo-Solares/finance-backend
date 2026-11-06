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
import com.finance.backend.modules.debts.statemententry.repository.StatementEntryRepository;
import com.finance.backend.modules.debts.usercard.model.UserCard;
import com.finance.backend.modules.debts.usercard.repository.UserCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        public List<StatementResponse> findAll(
                        String email) {

                List<Statement> statements = statementRepository
                                .findByUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                email);
                refreshPaymentStatuses(statements);
                return statements.stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY USER CARD
        // ===================

        public List<StatementResponse> findByUserCardId(
                        Long userCardId,
                        String email) {

                getOwnedUserCard(
                                userCardId,
                                email);

                List<Statement> statements = statementRepository
                                .findByUserCardUserCardIdAndUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                userCardId,
                                                email);
                refreshPaymentStatuses(statements);
                return statements.stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        // ===================
        // FIND BY ID
        // ===================

        public StatementResponse findById(
                        Long statementId,
                        String email) {

                Statement statement = getOwnedStatement(statementId, email);
                updatePaymentStatus(statement);
                return StatementMapper.toResponse(statement);
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

                Statement statement = StatementMapper.toEntity(
                                request,
                                userCard);

                statement.setPaid(
                                false);

                statement.setStatus(
                                StatementStatus.PAYMENT_PENDING);

                Statement savedStatement = statementRepository.save(
                                statement);

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

                updatePaymentStatus(statement);

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

                List<StatementEntry> entries = statementEntryRepository
                                .findByStatementStatementId(statementId);
                entries.forEach(entry -> entry.setPaid(Boolean.TRUE.equals(paid)));
                if (!entries.isEmpty()) {
                        statementEntryRepository.saveAll(entries);
                }
                updatePaymentStatus(statement);

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

                List<Long> statementIds = statements.stream()
                                .map(Statement::getStatementId)
                                .toList();
                if (!statementIds.isEmpty()) {
                        List<StatementEntry> entries = statementEntryRepository
                                        .findByStatementStatementIdIn(statementIds);
                        entries.forEach(entry -> entry.setPaid(true));
                        if (!entries.isEmpty()) {
                                statementEntryRepository.saveAll(entries);
                        }
                }

                refreshPaymentStatuses(statements);

                return statements
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        // ===================
        // REFRESH STATUS
        // ===================

        public void refreshStatuses() {
                List<Statement> statements = statementRepository.findAll();
                refreshPaymentStatuses(statements);
        }

        public void refreshPaymentStatuses(String email) {
                List<Statement> statements = statementRepository
                                .findByUserCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(email);
                refreshPaymentStatuses(statements);
        }

        public void refreshPaymentStatus(Long statementId) {
                Statement statement = statementRepository.findById(statementId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Estado de cuenta no encontrado"));
                if (updatePaymentStatus(statement)) {
                        statementRepository.save(statement);
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

        private boolean updatePaymentStatus(Statement statement) {
                Long statementId = statement.getStatementId();
                long entryCount = statementEntryRepository.countByStatementStatementId(statementId);
                long unpaidCount = statementEntryRepository
                                .countByStatementStatementIdAndPaidFalse(statementId);
                boolean allEntriesPaid = entryCount > 0 && unpaidCount == 0;
                StatementStatus status = allEntriesPaid
                                ? StatementStatus.CLOSED
                                : StatementStatus.PAYMENT_PENDING;
                boolean changed = !Boolean.valueOf(allEntriesPaid).equals(statement.getPaid())
                                || statement.getStatus() != status;
                statement.setPaid(allEntriesPaid);
                statement.setStatus(status);
                return changed;
        }

        private void refreshPaymentStatuses(List<Statement> statements) {
                List<Statement> changedStatements = new ArrayList<>();
                for (Statement statement : statements) {
                        if (updatePaymentStatus(statement)) {
                                changedStatements.add(statement);
                        }
                }
                if (!changedStatements.isEmpty()) {
                        statementRepository.saveAll(changedStatements);
                }
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

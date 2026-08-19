package com.finance.backend.modules.debts.statement.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.card.repository.CardRepository;
import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.mapper.StatementMapper;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.model.StatementSource;
import com.finance.backend.modules.debts.statement.model.StatementStatus;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@Transactional
public class StatementService {

        private final StatementRepository statementRepository;
        private final CardRepository cardRepository;

        public StatementService(
                        StatementRepository statementRepository,
                        CardRepository cardRepository) {
                this.statementRepository = statementRepository;
                this.cardRepository = cardRepository;
        }

        @Transactional(readOnly = true)
        public List<StatementResponse> findAll() {
                return statementRepository
                                .findAll()
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<StatementResponse> findByCardId(
                        Long cardId) {
                getCard(cardId);

                return statementRepository
                                .findByCardCardIdOrderByYearDescMonthDesc(cardId)
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public StatementResponse findById(
                        Long statementId) {
                return StatementMapper.toResponse(
                                getStatement(statementId));
        }

        @Transactional
        public StatementResponse create(
                        CreateStatementRequest request) {
                Card card = getCard(
                                request.cardId());

                Optional<Statement> existingStatement = statementRepository
                                .findByCardCardIdAndYearAndMonth(
                                                request.cardId(),
                                                request.year(),
                                                request.month());

                if (existingStatement.isPresent()) {
                        Statement statement = existingStatement.get();

                        if (statement.getSource() == StatementSource.ACTUAL) {
                                throw new ResponseStatusException(
                                                CONFLICT,
                                                "Ya existe un estado de cuenta real para "
                                                                + request.year()
                                                                + "-"
                                                                + request.month());
                        }

                        statement.setPeriodStart(
                                        request.periodStart());

                        statement.setPeriodEnd(
                                        request.periodEnd());

                        statement.setPaymentDate(
                                        request.paymentDate());

                        statement.setSource(
                                        StatementSource.ACTUAL);

                        statement.setStatus(
                                        calculateStatus(
                                                        statement,
                                                        LocalDate.now()));

                        Statement updatedStatement = statementRepository.save(
                                        statement);

                        recalculateProjectedStatements(
                                        card.getCardId());

                        return StatementMapper.toResponse(
                                        updatedStatement);
                }

                Statement statement = StatementMapper.toEntity(
                                request,
                                card);

                statement.setStatus(
                                calculateStatus(
                                                statement,
                                                LocalDate.now()));

                Statement savedStatement = statementRepository.save(
                                statement);

                recalculateProjectedStatements(
                                card.getCardId());

                return StatementMapper.toResponse(
                                savedStatement);
        }

        @Transactional
        public StatementResponse update(
                        Long statementId,
                        UpdateStatementRequest request) {
                Statement statement = getStatement(
                                statementId);

                Card card = getCard(
                                request.cardId());

                StatementMapper.updateEntity(
                                statement,
                                request,
                                card);

                statement.setStatus(
                                calculateStatus(
                                                statement,
                                                LocalDate.now()));

                Statement updatedStatement = statementRepository.save(
                                statement);

                recalculateProjectedStatements(
                                card.getCardId());

                return StatementMapper.toResponse(
                                updatedStatement);
        }

        public StatementResponse updatePaid(
                        Long statementId,
                        Boolean paid) {
                Statement statement = getStatement(
                                statementId);

                statement.setPaid(
                                paid);

                Statement updatedStatement = statementRepository.save(
                                statement);

                return StatementMapper.toResponse(
                                updatedStatement);
        }

        public List<StatementResponse> payAhead(
                        Long statementId,
                        Integer months) {
                Statement selectedStatement = getStatement(
                                statementId);

                Long cardId = selectedStatement
                                .getCard()
                                .getCardId();

                List<Statement> statements = statementRepository
                                .findByCardCardIdOrderByYearAscMonthAsc(
                                                cardId);

                int selectedIndex = -1;

                for (int index = 0; index < statements.size(); index++) {
                        if (statements
                                        .get(index)
                                        .getStatementId()
                                        .equals(statementId)) {
                                selectedIndex = index;
                                break;
                        }
                }

                if (selectedIndex == -1) {
                        throw new ResourceNotFoundException(
                                        "Estado de cuenta no encontrado");
                }

                int availableMonths = statements.size()
                                - selectedIndex;

                if (months > availableMonths) {
                        throw new ResponseStatusException(
                                        BAD_REQUEST,
                                        "No existen suficientes periodos para adelantar "
                                                        + months
                                                        + " meses. Periodos disponibles: "
                                                        + availableMonths);
                }

                List<Statement> statementsToUpdate = new ArrayList<>();

                for (int index = selectedIndex; index < selectedIndex + months; index++) {

                        Statement statement = statements.get(
                                        index);

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
                                .subList(
                                                selectedIndex,
                                                selectedIndex + months)
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        public List<StatementResponse> payAll(
                        Long cardId) {
                getCard(
                                cardId);

                List<Statement> statements = statementRepository
                                .findByCardCardIdOrderByYearAscMonthAsc(
                                                cardId);

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

        @Transactional
        public List<Statement> ensureProjectedStatements(
                        Statement baseStatement,
                        Integer monthsAhead) {

                if (monthsAhead == null || monthsAhead <= 0) {
                        return List.of();
                }

                List<Statement> result = new ArrayList<>();

                Statement previousStatement = baseStatement;

                YearMonth baseMonth = YearMonth.of(
                                baseStatement.getYear(),
                                baseStatement.getMonth());

                for (int offset = 1; offset <= monthsAhead; offset++) {

                        YearMonth targetMonth = baseMonth.plusMonths(
                                        offset);

                        Optional<Statement> existing = statementRepository
                                        .findByCardCardIdAndYearAndMonth(
                                                        baseStatement.getCard().getCardId(),
                                                        targetMonth.getYear(),
                                                        targetMonth.getMonthValue());

                        Statement statement;

                        if (existing.isPresent()) {
                                statement = existing.get();

                                if (statement.getSource() == StatementSource.PROJECTED) {
                                        updateProjectedDates(
                                                        statement,
                                                        previousStatement);

                                        statement.setStatus(
                                                        calculateStatus(
                                                                        statement,
                                                                        LocalDate.now()));

                                        statement = statementRepository.save(
                                                        statement);
                                }
                        } else {
                                statement = new Statement();

                                statement.setCard(
                                                baseStatement.getCard());

                                statement.setYear(
                                                targetMonth.getYear());

                                statement.setMonth(
                                                targetMonth.getMonthValue());

                                statement.setSource(
                                                StatementSource.PROJECTED);

                                statement.setPaid(
                                                false);

                                updateProjectedDates(
                                                statement,
                                                previousStatement);

                                statement.setStatus(
                                                calculateStatus(
                                                                statement,
                                                                LocalDate.now()));

                                statement = statementRepository.save(
                                                statement);
                        }

                        result.add(
                                        statement);

                        previousStatement = statement;
                }

                return result;
        }

        @Transactional
        public void refreshStatuses() {
                LocalDate today = LocalDate.now();

                List<Statement> statements = statementRepository
                                .findAll();

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

        public void delete(
                        Long statementId) {
                Statement statement = getStatement(
                                statementId);

                statementRepository.delete(
                                statement);
        }

        private void recalculateProjectedStatements(
                        Long cardId) {

                List<Statement> statements = statementRepository
                                .findByCardCardIdOrderByYearAscMonthAsc(
                                                cardId);

                Statement previousStatement = null;

                List<Statement> changedStatements = new ArrayList<>();

                for (Statement statement : statements) {

                        if (statement.getSource() == StatementSource.ACTUAL) {
                                previousStatement = statement;
                                continue;
                        }

                        if (previousStatement == null) {
                                throw new IllegalStateException(
                                                "Existe un periodo PROJECTED sin un periodo anterior que permita calcular sus fechas");
                        }

                        updateProjectedDates(
                                        statement,
                                        previousStatement);

                        statement.setStatus(
                                        calculateStatus(
                                                        statement,
                                                        LocalDate.now()));

                        changedStatements.add(
                                        statement);

                        previousStatement = statement;
                }

                if (!changedStatements.isEmpty()) {
                        statementRepository.saveAll(
                                        changedStatements);
                }
        }

        private void updateProjectedDates(
                        Statement projected,
                        Statement previous) {

                if (previous.getPeriodEnd() == null
                                || previous.getPaymentDate() == null) {
                        throw new IllegalStateException(
                                        "No se pueden proyectar fechas desde un periodo sin periodEnd y paymentDate");
                }

                projected.setPeriodStart(
                                previous.getPeriodEnd());

                projected.setPeriodEnd(
                                previous.getPeriodEnd().plusMonths(1));

                projected.setPaymentDate(
                                previous.getPaymentDate().plusMonths(1));
        }

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

        private Statement getStatement(
                        Long statementId) {
                return statementRepository
                                .findById(statementId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Estado de cuenta no encontrado"));
        }

        private Card getCard(
                        Long cardId) {
                return cardRepository
                                .findById(cardId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta no encontrada"));
        }
}
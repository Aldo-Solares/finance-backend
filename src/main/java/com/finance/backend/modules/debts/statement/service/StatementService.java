// src/main/java/com/finance/backend/modules/debts/statement/service/StatementService.java

package com.finance.backend.modules.debts.statement.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.card.repository.CardRepository;
import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.mapper.StatementMapper;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statement.model.StatementStatus;
import com.finance.backend.modules.debts.statement.repository.StatementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        public List<StatementResponse> findAll(
                        String email) {

                return statementRepository
                                .findByCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                email)
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<StatementResponse> findByCardId(
                        Long cardId,
                        String email) {

                getOwnedCard(
                                cardId,
                                email);

                return statementRepository
                                .findByCardCardIdAndCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                cardId,
                                                email)
                                .stream()
                                .map(StatementMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public StatementResponse findById(
                        Long statementId,
                        String email) {

                return StatementMapper.toResponse(
                                getOwnedStatement(
                                                statementId,
                                                email));
        }

        @Transactional
        public StatementResponse create(
                        CreateStatementRequest request,
                        String email) {

                Card card = getOwnedCard(
                                request.cardId(),
                                email);

                boolean exists = statementRepository
                                .existsByCardCardIdAndYearAndMonth(
                                                request.cardId(),
                                                request.year(),
                                                request.month());

                if (exists) {
                        throw new ResponseStatusException(
                                        CONFLICT,
                                        "Ya existe un estado de cuenta para "
                                                        + request.year()
                                                        + "-"
                                                        + request.month());
                }

                Statement statement = StatementMapper.toEntity(
                                request,
                                card);

                statement.setPaid(false);

                statement.setStatus(
                                calculateStatus(
                                                statement,
                                                LocalDate.now()));

                Statement savedStatement = statementRepository.save(
                                statement);

                return StatementMapper.toResponse(
                                savedStatement);
        }

        @Transactional
        public StatementResponse update(
                        Long statementId,
                        UpdateStatementRequest request,
                        String email) {

                Statement statement = getOwnedStatement(
                                statementId,
                                email);

                Card card = getOwnedCard(
                                request.cardId(),
                                email);

                boolean periodChanged = !statement.getCard()
                                .getCardId()
                                .equals(request.cardId())
                                || !statement.getYear()
                                                .equals(request.year())
                                || !statement.getMonth()
                                                .equals(request.month());

                if (periodChanged) {

                        boolean exists = statementRepository
                                        .existsByCardCardIdAndYearAndMonth(
                                                        request.cardId(),
                                                        request.year(),
                                                        request.month());

                        if (exists) {
                                throw new ResponseStatusException(
                                                CONFLICT,
                                                "Ya existe un estado de cuenta para "
                                                                + request.year()
                                                                + "-"
                                                                + request.month());
                        }
                }

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

                return StatementMapper.toResponse(
                                updatedStatement);
        }

        @Transactional
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

        @Transactional
        public List<StatementResponse> payAll(
                        Long cardId,
                        String email) {

                getOwnedCard(
                                cardId,
                                email);

                List<Statement> statements = statementRepository
                                .findByCardCardIdAndCardUserEmailIgnoreCaseOrderByYearDescMonthDesc(
                                                cardId,
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

        @Transactional
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

        @Transactional
        public void delete(
                        Long statementId,
                        String email) {

                Statement statement = getOwnedStatement(
                                statementId,
                                email);

                statementRepository.delete(
                                statement);
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

        private Statement getOwnedStatement(
                        Long statementId,
                        String email) {

                return statementRepository
                                .findByStatementIdAndCardUserEmailIgnoreCase(
                                                statementId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Estado de cuenta no encontrado"));
        }

        private Card getOwnedCard(
                        Long cardId,
                        String email) {

                return cardRepository
                                .findByCardIdAndUserEmailIgnoreCase(
                                                cardId,
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Tarjeta no encontrada"));
        }
}
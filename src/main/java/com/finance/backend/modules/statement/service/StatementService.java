package com.finance.backend.modules.statement.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.card.model.Card;
import com.finance.backend.modules.card.repository.CardRepository;
import com.finance.backend.modules.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.statement.dto.StatementResponse;
import com.finance.backend.modules.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.statement.mapper.StatementMapper;
import com.finance.backend.modules.statement.model.Statement;
import com.finance.backend.modules.statement.repository.StatementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatementService {

    private final StatementRepository statementRepository;
    private final CardRepository cardRepository;

    public StatementService(
            StatementRepository statementRepository,
            CardRepository cardRepository) {
        this.statementRepository = statementRepository;
        this.cardRepository = cardRepository;
    }

    public List<StatementResponse> findAll() {
        return statementRepository
                .findAll()
                .stream()
                .map(StatementMapper::toResponse)
                .toList();
    }

    public List<StatementResponse> findByCardId(
            Long cardId) {
        getCard(cardId);

        return statementRepository
                .findByCardCardIdOrderByYearDescMonthDesc(cardId)
                .stream()
                .map(StatementMapper::toResponse)
                .toList();
    }

    public StatementResponse findById(
            Long statementId) {
        return StatementMapper.toResponse(
                getStatement(statementId));
    }

    public StatementResponse create(
            CreateStatementRequest request) {
        Card card = getCard(
                request.cardId());

        Statement statement = StatementMapper.toEntity(
                request,
                card);

        Statement savedStatement = statementRepository.save(statement);

        return StatementMapper.toResponse(
                savedStatement);
    }

    public StatementResponse update(
            Long statementId,
            UpdateStatementRequest request) {
        Statement statement = getStatement(statementId);

        Card card = getCard(
                request.cardId());

        StatementMapper.updateEntity(
                statement,
                request,
                card);

        Statement updatedStatement = statementRepository.save(statement);

        return StatementMapper.toResponse(
                updatedStatement);
    }

    public void delete(
            Long statementId) {
        Statement statement = getStatement(statementId);

        statementRepository.delete(statement);
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
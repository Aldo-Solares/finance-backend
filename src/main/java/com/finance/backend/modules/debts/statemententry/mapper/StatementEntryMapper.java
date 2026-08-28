package com.finance.backend.modules.debts.statemententry.mapper;

import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;

public final class StatementEntryMapper {

    private StatementEntryMapper() {
    }

    public static StatementEntry toEntity(
            CreateStatementEntryRequest request,
            Statement statement,
            Concept concept) {

        StatementEntry entry = new StatementEntry();

        entry.setStatement(statement);
        entry.setConcept(concept);
        entry.setDebtor(request.debtor());
        entry.setDescription(request.description());
        entry.setEntryType(request.entryType());
        entry.setDate(request.date());
        entry.setAmount(request.amount());
        entry.setPaid(request.paid());
        entry.setMsiCurrent(request.msiCurrent());
        entry.setMsiTotal(request.msiTotal());
        entry.setPurchaseAmount(request.purchaseAmount());
        entry.setRemainingMsi(request.remainingMsi());
        entry.setRemainingMsiAmount(request.remainingMsiAmount());

        return entry;
    }

    public static void updateEntity(
            StatementEntry entry,
            UpdateStatementEntryRequest request,
            Statement statement,
            Concept concept) {

        entry.setStatement(statement);
        entry.setConcept(concept);
        entry.setDebtor(request.debtor());
        entry.setDescription(request.description());
        entry.setEntryType(request.entryType());
        entry.setDate(request.date());
        entry.setAmount(request.amount());
        entry.setPaid(request.paid());
        entry.setMsiCurrent(request.msiCurrent());
        entry.setMsiTotal(request.msiTotal());
        entry.setPurchaseAmount(request.purchaseAmount());
        entry.setRemainingMsi(request.remainingMsi());
        entry.setRemainingMsiAmount(request.remainingMsiAmount());
    }

    public static StatementEntryResponse toResponse(
            StatementEntry entry) {

        return new StatementEntryResponse(
                entry.getEntryId(),
                entry.getStatement().getStatementId(),
                entry.getConcept().getConceptId(),
                entry.getConcept().getName(),
                entry.getDebtor(),
                entry.getDescription(),
                entry.getEntryType(),
                entry.getDate(),
                entry.getAmount(),
                entry.getPaid(),
                entry.getMsiCurrent(),
                entry.getMsiTotal(),
                entry.getPurchaseAmount(),
                entry.getRemainingMsi(),
                entry.getRemainingMsiAmount());
    }
}
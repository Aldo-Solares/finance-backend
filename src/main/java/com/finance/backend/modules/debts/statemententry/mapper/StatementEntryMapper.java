package com.finance.backend.modules.debts.statemententry.mapper;

import com.finance.backend.modules.debts.concept.model.Concept;
import com.finance.backend.modules.debts.statement.model.Statement;
import com.finance.backend.modules.debts.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.debts.statemententry.model.StatementEntry;
import com.finance.backend.modules.debts.statemententry.model.StatementEntrySource;

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
        entry.setPurchaseDate(request.purchaseDate());
        entry.setInstallmentAmount(request.installmentAmount());
        entry.setPaid(request.paid());
        entry.setMsiCurrent(request.msiCurrent());
        entry.setMsiTotal(request.msiTotal());
        entry.setPurchaseTotal(request.purchaseTotal());
        entry.setRemainingMonths(request.remainingMonths());
        entry.setRemainingTotal(request.remainingTotal());
        entry.setSource(StatementEntrySource.ACTUAL);

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
        entry.setPurchaseDate(request.purchaseDate());
        entry.setInstallmentAmount(request.installmentAmount());
        entry.setPaid(request.paid());
        entry.setMsiCurrent(request.msiCurrent());
        entry.setMsiTotal(request.msiTotal());
        entry.setPurchaseTotal(request.purchaseTotal());
        entry.setRemainingMonths(request.remainingMonths());
        entry.setRemainingTotal(request.remainingTotal());
    }

    public static StatementEntryResponse toResponse(
            StatementEntry entry) {
        return new StatementEntryResponse(
                entry.getEntryId(),
                entry.getStatement().getStatementId(),
                entry.getConcept().getConceptId(),
                entry.getDebtor(),
                entry.getDescription(),
                entry.getPurchaseDate(),
                entry.getInstallmentAmount(),
                entry.getPaid(),
                entry.getMsiCurrent(),
                entry.getMsiTotal(),
                entry.getPurchaseTotal(),
                entry.getRemainingMonths(),
                entry.getRemainingTotal(),
                entry.getSource());
    }

    public static void updateEntity(
            StatementEntry entry,
            CreateStatementEntryRequest request,
            Statement statement,
            Concept concept) {

        entry.setStatement(statement);
        entry.setConcept(concept);
        entry.setDebtor(request.debtor());
        entry.setDescription(request.description());
        entry.setPurchaseDate(request.purchaseDate());
        entry.setInstallmentAmount(request.installmentAmount());
        entry.setPaid(request.paid());
        entry.setMsiCurrent(request.msiCurrent());
        entry.setMsiTotal(request.msiTotal());
        entry.setPurchaseTotal(request.purchaseTotal());
        entry.setRemainingMonths(request.remainingMonths());
        entry.setRemainingTotal(request.remainingTotal());
        entry.setSource(StatementEntrySource.ACTUAL);
    }
}
package com.finance.backend.modules.statemententry.mapper;

import com.finance.backend.modules.concept.model.Concept;
import com.finance.backend.modules.statemententry.dto.CreateStatementEntryRequest;
import com.finance.backend.modules.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.statemententry.dto.UpdateStatementEntryRequest;
import com.finance.backend.modules.statemententry.model.StatementEntry;
import com.finance.backend.modules.statement.model.Statement;
import com.finance.backend.modules.user.model.User;

public final class StatementEntryMapper {

    private StatementEntryMapper() {
    }

    public static StatementEntry toEntity(
            CreateStatementEntryRequest request,
            Statement statement,
            Concept concept,
            User user) {
        StatementEntry entry = new StatementEntry();

        entry.setStatement(statement);
        entry.setConcept(concept);
        entry.setUser(user);

        entry.setDescription(request.description());
        entry.setPurchaseDate(request.purchaseDate());
        entry.setInstallmentAmount(request.installmentAmount());

        entry.setPaid(
                request.paid() != null
                        ? request.paid()
                        : false);

        entry.setMsiCurrent(request.msiCurrent());
        entry.setMsiTotal(request.msiTotal());
        entry.setPurchaseTotal(request.purchaseTotal());
        entry.setRemainingMonths(request.remainingMonths());
        entry.setRemainingTotal(request.remainingTotal());

        return entry;
    }

    public static void updateEntity(
            StatementEntry entry,
            UpdateStatementEntryRequest request,
            Statement statement,
            Concept concept,
            User user) {
        entry.setStatement(statement);
        entry.setConcept(concept);
        entry.setUser(user);

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
                entry.getConcept().getName(),

                entry.getUser().getUserId(),
                entry.getUser().getName(),

                entry.getDescription(),
                entry.getPurchaseDate(),

                entry.getInstallmentAmount(),

                entry.getPaid(),

                entry.getMsiCurrent(),
                entry.getMsiTotal(),

                entry.getPurchaseTotal(),

                entry.getRemainingMonths(),
                entry.getRemainingTotal());
    }
}
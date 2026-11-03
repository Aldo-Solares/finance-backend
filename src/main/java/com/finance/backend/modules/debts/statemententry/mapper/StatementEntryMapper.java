// StatementEntryMapper.java

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
        entry.setCategory(concept);
        entry.setDebtor(request.debtor());
        entry.setDescription(request.description());
        entry.setMerchant(request.merchant());
        entry.setNotes(request.notes());
        entry.setEntryType(request.entryType());
        entry.setDate(request.date());
        entry.setAmount(request.amount());
        entry.setPaid(request.paid());
        entry.setInstallmentNumber(request.installmentNumber());
        entry.setTotalInstallments(request.totalInstallments());

        return entry;
    }

    public static void updateEntity(
            StatementEntry entry,
            UpdateStatementEntryRequest request,
            Statement statement,
            Concept concept) {

        entry.setStatement(statement);
        entry.setCategory(concept);
        entry.setDebtor(request.debtor());
        entry.setDescription(request.description());
        entry.setMerchant(request.merchant());
        entry.setNotes(request.notes());
        entry.setEntryType(request.entryType());
        entry.setDate(request.date());
        entry.setAmount(request.amount());
        entry.setPaid(request.paid());
        entry.setInstallmentNumber(request.installmentNumber());
        entry.setTotalInstallments(request.totalInstallments());
    }

    public static StatementEntryResponse toResponse(
            StatementEntry entry) {

        return new StatementEntryResponse(
                entry.getEntryId(),
                entry.getStatement().getStatementId(),
                entry.getCategory().getCategoryId(),
                entry.getCategory().getName(),
                entry.getDebtor(),
                entry.getDescription(),
                entry.getMerchant(),
                entry.getNotes(),
                entry.getEntryType(),
                entry.getDate(),
                entry.getAmount(),
                entry.getPaid(),
                entry.getInstallmentNumber(),
                entry.getTotalInstallments(),
                entry.getPurchaseAmount(),
                entry.getRemainingInstallments(),
                entry.getRemainingInstallmentsAmount());
    }
}
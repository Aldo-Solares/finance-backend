package com.finance.backend.modules.debts.statement.mapper;

import com.finance.backend.modules.debts.card.model.Card;
import com.finance.backend.modules.debts.statement.dto.CreateStatementRequest;
import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statement.dto.UpdateStatementRequest;
import com.finance.backend.modules.debts.statement.model.Statement;

public final class StatementMapper {

    private StatementMapper() {
    }

    public static Statement toEntity(
            CreateStatementRequest request,
            Card card) {
        Statement statement = new Statement();

        statement.setCard(card);
        statement.setYear(request.year());
        statement.setMonth(request.month());
        statement.setPeriodStart(request.periodStart());
        statement.setPeriodEnd(request.periodEnd());
        statement.setPaymentDate(request.paymentDate());

        return statement;
    }

    public static void updateEntity(
            Statement statement,
            UpdateStatementRequest request,
            Card card) {
        statement.setCard(card);
        statement.setYear(request.year());
        statement.setMonth(request.month());
        statement.setPeriodStart(request.periodStart());
        statement.setPeriodEnd(request.periodEnd());
        statement.setPaymentDate(request.paymentDate());
        statement.setNotes(request.notes());
    }

    public static StatementResponse toResponse(
            Statement statement) {
        return new StatementResponse(
                statement.getStatementId(),
                statement.getCard().getCardId(),
                statement.getCard().getCardCode(),
                statement.getYear(),
                statement.getMonth(),
                statement.getPeriodStart(),
                statement.getPeriodEnd(),
                statement.getPaymentDate(),
                statement.getStatus(),
                statement.getPaid(),
                statement.getNotes());
    }
}
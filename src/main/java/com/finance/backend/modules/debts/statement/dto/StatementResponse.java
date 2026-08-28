package com.finance.backend.modules.debts.statement.dto;

import com.finance.backend.modules.debts.statement.model.StatementStatus;

import java.time.LocalDate;

public record StatementResponse(
        Long statementId,
        Long userCardId,
        Long cardId,
        String bank,
        String cardName,
        Integer year,
        Integer month,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDate paymentDate,
        StatementStatus status,
        Boolean paid,
        String notes) {
}
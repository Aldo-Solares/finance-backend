package com.finance.backend.modules.statement.dto;

import java.time.LocalDate;

public record StatementResponse(
        Long statementId,

        Long cardId,
        String cardCode,

        Integer year,
        Integer month,

        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDate paymentDate) {
}
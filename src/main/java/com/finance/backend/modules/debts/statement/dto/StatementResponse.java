package com.finance.backend.modules.debts.statement.dto;

import com.finance.backend.modules.debts.statement.model.StatementSource;
import com.finance.backend.modules.debts.statement.model.StatementStatus;

import java.time.LocalDate;

public record StatementResponse(
                Long statementId,
                Long cardId,
                String cardCode,
                Integer year,
                Integer month,
                LocalDate periodStart,
                LocalDate periodEnd,
                LocalDate paymentDate,
                StatementStatus status,
                StatementSource source,
                Boolean paid,
                String notes) {
}
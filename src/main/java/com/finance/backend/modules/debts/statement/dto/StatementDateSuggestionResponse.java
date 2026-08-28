package com.finance.backend.modules.debts.statement.dto;

import java.time.LocalDate;

public record StatementDateSuggestionResponse(
                LocalDate periodStart,
                LocalDate periodEnd,
                LocalDate paymentDate) {
}
package com.finance.backend.modules.debts.statemententry.dto;

import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateStatementEntryRequest(
        @NotNull Long statementId,
        @NotNull Long conceptId,
        @NotBlank String debtor,
        String specification,
        String notes,
        @NotNull StatementEntryType entryType,
        LocalDate date,
        @NotNull @Positive BigDecimal amount,
        @NotNull Boolean paid,
        Integer msiCurrent,
        Integer msiTotal) {
}
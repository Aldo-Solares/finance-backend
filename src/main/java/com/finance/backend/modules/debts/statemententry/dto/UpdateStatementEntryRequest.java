package com.finance.backend.modules.debts.statemententry.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateStatementEntryRequest(

        @NotNull Long statementId,

        @NotNull Long conceptId,

        @NotNull String debtor,

        String description,

        LocalDate purchaseDate,

        @PositiveOrZero BigDecimal installmentAmount,

        @NotNull Boolean paid,

        @PositiveOrZero Integer msiCurrent,

        @PositiveOrZero Integer msiTotal,

        @PositiveOrZero BigDecimal purchaseTotal,

        @PositiveOrZero Integer remainingMonths,

        @PositiveOrZero BigDecimal remainingTotal

) {
}
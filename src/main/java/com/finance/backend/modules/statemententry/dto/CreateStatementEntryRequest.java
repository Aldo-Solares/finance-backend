package com.finance.backend.modules.statemententry.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateStatementEntryRequest(

        @NotNull Long statementId,

        @NotNull Long conceptId,

        @NotNull Long userId,

        String description,

        LocalDate purchaseDate,

        @PositiveOrZero BigDecimal installmentAmount,

        Boolean paid,

        @PositiveOrZero Integer msiCurrent,

        @PositiveOrZero Integer msiTotal,

        @PositiveOrZero BigDecimal purchaseTotal,

        @PositiveOrZero Integer remainingMonths,

        @PositiveOrZero BigDecimal remainingTotal

) {
}
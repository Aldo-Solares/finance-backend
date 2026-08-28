package com.finance.backend.modules.debts.statement.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateStatementRequest(

        @NotNull Long userCardId,

        @NotNull LocalDate periodStart,

        @NotNull LocalDate periodEnd,

        @NotNull LocalDate paymentDate,

        String notes

) {
}
package com.finance.backend.modules.debts.statement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateStatementRequest(

                @NotNull Long cardId,

                @NotNull Integer year,

                @NotNull @Min(1) @Max(12) Integer month,

                LocalDate periodStart,

                LocalDate periodEnd,

                LocalDate paymentDate) {
}
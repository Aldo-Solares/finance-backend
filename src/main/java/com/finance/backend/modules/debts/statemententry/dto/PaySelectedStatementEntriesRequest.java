package com.finance.backend.modules.debts.statemententry.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PaySelectedStatementEntriesRequest(
        @NotEmpty(message = "Selecciona al menos un movimiento")
        List<@NotNull @Positive Long> entryIds) {
}

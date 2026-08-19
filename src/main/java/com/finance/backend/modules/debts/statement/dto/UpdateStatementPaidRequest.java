package com.finance.backend.modules.debts.statement.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateStatementPaidRequest(

        @NotNull Boolean paid) {
}
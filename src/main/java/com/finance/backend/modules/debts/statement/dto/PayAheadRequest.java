package com.finance.backend.modules.debts.statement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PayAheadRequest(

                @NotNull @Min(1) Integer months) {
}
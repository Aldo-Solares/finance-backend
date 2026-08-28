package com.finance.backend.modules.debts.usercard.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateUserCardRequest(

        @NotNull Boolean active

) {
}
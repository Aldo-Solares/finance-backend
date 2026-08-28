package com.finance.backend.modules.debts.usercard.dto;

import jakarta.validation.constraints.NotNull;

public record CreateUserCardRequest(

        @NotNull Long cardId,

        @NotNull Boolean active

) {
}
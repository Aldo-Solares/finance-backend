package com.finance.backend.modules.debts.card.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequest(

        @NotBlank String bank,

        @NotBlank String cardName

) {
}
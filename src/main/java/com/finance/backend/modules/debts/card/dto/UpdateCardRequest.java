package com.finance.backend.modules.debts.card.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCardRequest(

        @NotBlank String bank,

        @NotBlank String cardName

) {
}
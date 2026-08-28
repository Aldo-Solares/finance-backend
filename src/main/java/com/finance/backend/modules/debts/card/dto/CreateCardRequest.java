package com.finance.backend.modules.debts.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCardRequest(

                @NotBlank String bank,

                @NotBlank String cardName,

                @NotNull Boolean active

) {
}
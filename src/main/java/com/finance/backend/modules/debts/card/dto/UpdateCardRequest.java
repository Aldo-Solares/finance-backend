package com.finance.backend.modules.debts.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCardRequest(

        @NotBlank String bank,

        @NotBlank String cardName,

        @Size(max = 100) String defaultMerchant

) {
}

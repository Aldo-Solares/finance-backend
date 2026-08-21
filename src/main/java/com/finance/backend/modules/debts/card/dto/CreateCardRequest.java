package com.finance.backend.modules.debts.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCardRequest(

        @NotBlank @Size(max = 50) String cardCode,

        @NotNull Long productId,

        Boolean active

) {
}
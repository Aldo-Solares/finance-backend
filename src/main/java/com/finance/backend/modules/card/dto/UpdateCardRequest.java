package com.finance.backend.modules.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCardRequest(

        @NotBlank @Size(max = 50) String cardCode,

        @NotNull Long productId,

        @NotNull Long userId,

        @NotNull Boolean active

) {
}
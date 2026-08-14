package com.finance.backend.modules.cardproduct.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCardProductRequest(

        @NotBlank @Size(max = 100) String bank,

        @NotBlank @Size(max = 100) String cardName

) {
}
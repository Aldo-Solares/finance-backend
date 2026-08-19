package com.finance.backend.modules.debts.cardproduct.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCardProductRequest(

                @NotBlank @Size(max = 100) String bank,

                @NotBlank @Size(max = 100) String cardName

) {
}
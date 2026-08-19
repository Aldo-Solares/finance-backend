package com.finance.backend.modules.debts.concept.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateConceptRequest(

                @NotBlank @Size(max = 100) String name

) {
}
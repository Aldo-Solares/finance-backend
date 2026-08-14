package com.finance.backend.modules.user.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(

        @NotBlank String token

) {
}
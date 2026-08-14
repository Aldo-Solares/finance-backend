package com.finance.backend.modules.user.dto.auth;

import com.finance.backend.modules.user.utils.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

        @NotBlank String token,

        @NotBlank @StrongPassword String newPassword

) {
}
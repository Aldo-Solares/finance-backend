package com.finance.backend.modules.user.dto.user;

import com.finance.backend.modules.user.utils.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

        @NotBlank String currentPassword,

        @NotBlank @StrongPassword String newPassword

) {
}
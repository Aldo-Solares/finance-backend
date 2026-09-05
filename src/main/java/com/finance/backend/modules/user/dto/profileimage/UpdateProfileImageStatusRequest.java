package com.finance.backend.modules.user.dto.profileimage;

import jakarta.validation.constraints.NotNull;

public record UpdateProfileImageStatusRequest(
        @NotNull Boolean active) {
}
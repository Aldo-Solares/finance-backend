package com.finance.backend.modules.user.dto.profileimage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileImageRequest(
                @NotBlank @Size(max = 100) String name) {
}
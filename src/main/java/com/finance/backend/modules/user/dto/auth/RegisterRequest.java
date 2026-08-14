package com.finance.backend.modules.user.dto.auth;

import com.finance.backend.modules.user.utils.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank @Size(max = 100) String name,

        @Size(max = 100) String lastName,

        @Size(max = 100) String secondLastName,

        @NotBlank @Email @Size(max = 150) String email,

        @NotBlank @StrongPassword String password

) {
}
package com.finance.backend.modules.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

                @NotBlank @Size(max = 100) String name,

                @Size(max = 100) String lastName,

                @Size(max = 100) String secondLastName,

                @NotBlank @Email @Size(max = 150) String email

) {
}
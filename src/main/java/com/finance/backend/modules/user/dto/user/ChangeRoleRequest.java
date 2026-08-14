package com.finance.backend.modules.user.dto.user;

import com.finance.backend.modules.user.model.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(

        @NotNull Role role

) {
}
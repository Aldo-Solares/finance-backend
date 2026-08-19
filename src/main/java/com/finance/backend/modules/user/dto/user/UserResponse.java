package com.finance.backend.modules.user.dto.user;

import com.finance.backend.modules.user.model.Role;

public record UserResponse(
                Long userId,
                String name,
                String lastName,
                String secondLastName,
                String email,
                Role role,
                Boolean emailVerified) {
}
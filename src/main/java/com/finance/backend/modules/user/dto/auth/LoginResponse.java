package com.finance.backend.modules.user.dto.auth;

import com.finance.backend.modules.user.model.Role;

public record LoginResponse(
        String token,
        Long userId,
        String email,
        String name,
        Role role) {
}
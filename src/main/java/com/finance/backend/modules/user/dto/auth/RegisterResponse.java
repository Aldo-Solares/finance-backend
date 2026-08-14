package com.finance.backend.modules.user.dto.auth;

import com.finance.backend.modules.user.dto.user.UserResponse;

public record RegisterResponse(
        UserResponse user,
        String verificationToken) {
}
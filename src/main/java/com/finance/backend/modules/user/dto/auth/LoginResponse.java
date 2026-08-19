package com.finance.backend.modules.user.dto.auth;

import com.finance.backend.modules.user.dto.user.UserResponse;

public record LoginResponse(
                String token,
                UserResponse user) {
}
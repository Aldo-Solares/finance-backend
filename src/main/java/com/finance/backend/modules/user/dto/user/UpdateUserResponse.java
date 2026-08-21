package com.finance.backend.modules.user.dto.user;

public record UpdateUserResponse(
                UserResponse user,
                String token) {
}
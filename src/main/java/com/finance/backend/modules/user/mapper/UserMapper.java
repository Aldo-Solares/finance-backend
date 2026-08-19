package com.finance.backend.modules.user.mapper;

import com.finance.backend.modules.user.dto.user.UpdateUserRequest;
import com.finance.backend.modules.user.dto.user.UserResponse;
import com.finance.backend.modules.user.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static void updateEntity(
            User user,
            UpdateUserRequest request) {
        user.setName(request.name().trim());
        user.setLastName(normalize(request.lastName()));
        user.setSecondLastName(normalize(request.secondLastName()));
        user.setEmail(request.email().trim().toLowerCase());
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getLastName(),
                user.getSecondLastName(),
                user.getEmail(),
                user.getRole(),
                user.getEmailVerified());
    }

    private static String normalize(
            String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
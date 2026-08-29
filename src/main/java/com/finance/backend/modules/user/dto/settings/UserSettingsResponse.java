package com.finance.backend.modules.user.dto.settings;

public record UserSettingsResponse(
        Long userSettingsId,
        Long userId,
        boolean statementCutoffReminder) {
}
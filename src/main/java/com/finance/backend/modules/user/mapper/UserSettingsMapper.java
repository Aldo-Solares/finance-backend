package com.finance.backend.modules.user.mapper;

import com.finance.backend.modules.user.dto.settings.UserSettingsResponse;
import com.finance.backend.modules.user.model.UserSettings;

public final class UserSettingsMapper {

    private UserSettingsMapper() {
    }

    // ===================
    // RESPONSE
    // ===================

    public static UserSettingsResponse toResponse(
            UserSettings userSettings) {

        return new UserSettingsResponse(
                userSettings.getUserSettingsId(),
                userSettings.getUserId(),
                userSettings.getStatementCutoffReminder());
    }

    // ===================
    // UPDATE ENTITY
    // ===================

    public static void updateEntity(
            UserSettings userSettings,
            Boolean statementCutoffReminder) {

        userSettings.setStatementCutoffReminder(
                statementCutoffReminder);
    }
}

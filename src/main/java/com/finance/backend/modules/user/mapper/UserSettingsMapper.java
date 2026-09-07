package com.finance.backend.modules.user.mapper;

import com.finance.backend.modules.user.dto.settings.UserSettingsResponse;
import com.finance.backend.modules.user.model.ProfileImageBackground;
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
                userSettings.getStatementCutoffReminder(),
                userSettings.getProfileImageBackground());
    }

    // ===================
    // UPDATE REMINDER
    // ===================

    public static void updateStatementCutoffReminder(
            UserSettings userSettings,
            Boolean statementCutoffReminder) {

        userSettings.setStatementCutoffReminder(
                statementCutoffReminder);
    }

    // ===================
    // UPDATE PROFILE IMAGE BACKGROUND
    // ===================

    public static void updateProfileImageBackground(
            UserSettings userSettings,
            ProfileImageBackground profileImageBackground) {

        userSettings.setProfileImageBackground(
                profileImageBackground);
    }
}
package com.finance.backend.modules.user.dto.settings;

import com.finance.backend.modules.user.model.ProfileImageBackground;

public record UserSettingsResponse(

        Long userSettingsId,

        Long userId,

        boolean statementCutoffReminder,

        ProfileImageBackground profileImageBackground,

        boolean useProfileImageBackgroundAsPrimaryColor

) {
}
package com.finance.backend.modules.user.dto.settings;

import jakarta.validation.constraints.NotNull;

public record UpdateUseProfileImageBackgroundAsPrimaryColorRequest(

        @NotNull(message = "La configuración del color principal es obligatoria") Boolean useProfileImageBackgroundAsPrimaryColor

) {
}
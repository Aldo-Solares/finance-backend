package com.finance.backend.modules.user.dto.settings;

import jakarta.validation.constraints.NotNull;

public record UpdateDarkModeRequest(

        @NotNull Boolean active) {

}
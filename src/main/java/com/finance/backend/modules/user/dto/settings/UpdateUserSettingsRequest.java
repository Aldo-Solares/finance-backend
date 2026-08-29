package com.finance.backend.modules.user.dto.settings;

import jakarta.validation.constraints.NotNull;

public record UpdateUserSettingsRequest(

                @NotNull(message = "La preferencia de recordatorio de fecha de corte es obligatoria") Boolean statementCutoffReminder

) {
}
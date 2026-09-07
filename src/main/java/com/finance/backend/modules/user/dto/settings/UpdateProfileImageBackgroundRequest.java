package com.finance.backend.modules.user.dto.settings;

import com.finance.backend.modules.user.model.ProfileImageBackground;
import jakarta.validation.constraints.NotNull;

public record UpdateProfileImageBackgroundRequest(

                @NotNull(message = "El fondo de la imagen de perfil es obligatorio") ProfileImageBackground profileImageBackground

) {
}
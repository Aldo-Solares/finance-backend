// @/modules/user/controller/UserSettingsController.java

package com.finance.backend.modules.user.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.user.dto.settings.UpdateProfileImageBackgroundRequest;
import com.finance.backend.modules.user.dto.settings.UpdateStatementCutoffReminderRequest;
import com.finance.backend.modules.user.dto.settings.UserSettingsResponse;
import com.finance.backend.modules.user.service.UserSettingsService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-settings")
public class UserSettingsController {

        private final UserSettingsService userSettingsService;

        public UserSettingsController(
                        UserSettingsService userSettingsService) {

                this.userSettingsService = userSettingsService;
        }

        // ===================
        // CONSULTA
        // ===================

        @GetMapping("/me")
        public ApiResponse<UserSettingsResponse> findCurrentUserSettings(
                        Authentication authentication) {

                return ApiResponse.success(
                                userSettingsService.findCurrentUserSettings(
                                                authentication.getName()));
        }

        // ===================
        // ACTUALIZAR REMINDER
        // ===================

        @PatchMapping("/me/statement-cutoff-reminder")
        public ApiResponse<UserSettingsResponse> updateStatementCutoffReminder(
                        Authentication authentication,
                        @Valid @RequestBody UpdateStatementCutoffReminderRequest request) {

                return ApiResponse.success(
                                "Preferencia de recordatorio actualizada",
                                userSettingsService.updateStatementCutoffReminder(
                                                authentication.getName(),
                                                request));
        }

        // ===================
        // ACTUALIZAR BACKGROUND
        // ===================

        @PatchMapping("/me/profile-image-background")
        public ApiResponse<UserSettingsResponse> updateProfileImageBackground(
                        Authentication authentication,
                        @Valid @RequestBody UpdateProfileImageBackgroundRequest request) {

                return ApiResponse.success(
                                "Fondo de imagen de perfil actualizado",
                                userSettingsService.updateProfileImageBackground(
                                                authentication.getName(),
                                                request));
        }
}
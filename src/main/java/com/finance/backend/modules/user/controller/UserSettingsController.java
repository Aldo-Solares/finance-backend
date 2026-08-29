package com.finance.backend.modules.user.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.user.dto.settings.UpdateUserSettingsRequest;
import com.finance.backend.modules.user.dto.settings.UserSettingsResponse;
import com.finance.backend.modules.user.service.UserSettingsService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    // ACTUALIZACIÓN
    // ===================

    @PatchMapping("/me")
    public ApiResponse<UserSettingsResponse> updateCurrentUserSettings(
            Authentication authentication,
            @Valid @RequestBody UpdateUserSettingsRequest request) {

        return ApiResponse.success(
                "Configuración actualizada",
                userSettingsService.updateCurrentUserSettings(
                        authentication.getName(),
                        request));
    }
}
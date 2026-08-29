package com.finance.backend.modules.user.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.user.dto.settings.UpdateUserSettingsRequest;
import com.finance.backend.modules.user.dto.settings.UserSettingsResponse;
import com.finance.backend.modules.user.mapper.UserSettingsMapper;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.model.UserSettings;
import com.finance.backend.modules.user.repository.UserRepository;
import com.finance.backend.modules.user.repository.UserSettingsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public UserSettingsService(
            UserSettingsRepository userSettingsRepository,
            UserRepository userRepository) {

        this.userSettingsRepository = userSettingsRepository;

        this.userRepository = userRepository;
    }

    // ===================
    // CONSULTA
    // ===================

    @Transactional(readOnly = true)
    public UserSettingsResponse findCurrentUserSettings(
            String email) {

        User user = getUserByEmail(email);

        UserSettings userSettings = userSettingsRepository
                .findByUserId(user.getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Configuración del usuario no encontrada"));

        return UserSettingsMapper.toResponse(
                userSettings);
    }

    // ===================
    // ACTUALIZACIÓN
    // ===================

    @Transactional
    public UserSettingsResponse updateCurrentUserSettings(
            String email,
            UpdateUserSettingsRequest request) {

        User user = getUserByEmail(email);

        UserSettings userSettings = userSettingsRepository
                .findByUserId(user.getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Configuración del usuario no encontrada"));

        UserSettingsMapper.updateEntity(
                userSettings,
                request.statementCutoffReminder());

        UserSettings savedUserSettings = userSettingsRepository.save(
                userSettings);

        return UserSettingsMapper.toResponse(
                savedUserSettings);
    }

    // ===================
    // HELPERS
    // ===================

    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario no encontrado"));
    }
}
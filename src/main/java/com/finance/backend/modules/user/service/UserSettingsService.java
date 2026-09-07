package com.finance.backend.modules.user.service;

import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.user.dto.settings.UpdateProfileImageBackgroundRequest;
import com.finance.backend.modules.user.dto.settings.UpdateStatementCutoffReminderRequest;
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

                UserSettings userSettings = getUserSettings(user.getUserId());

                return UserSettingsMapper.toResponse(
                                userSettings);
        }

        // ===================
        // ACTUALIZAR REMINDER
        // ===================

        @Transactional
        public UserSettingsResponse updateStatementCutoffReminder(
                        String email,
                        UpdateStatementCutoffReminderRequest request) {

                User user = getUserByEmail(email);

                UserSettings userSettings = getUserSettings(
                                user.getUserId());

                UserSettingsMapper.updateStatementCutoffReminder(
                                userSettings,
                                request.statementCutoffReminder());

                UserSettings savedUserSettings = userSettingsRepository.save(
                                userSettings);

                return UserSettingsMapper.toResponse(
                                savedUserSettings);
        }

        // ===================
        // ACTUALIZAR BACKGROUND
        // ===================

        @Transactional
        public UserSettingsResponse updateProfileImageBackground(
                        String email,
                        UpdateProfileImageBackgroundRequest request) {

                User user = getUserByEmail(email);

                UserSettings userSettings = getUserSettings(
                                user.getUserId());

                UserSettingsMapper.updateProfileImageBackground(
                                userSettings,
                                request.profileImageBackground());

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

        private UserSettings getUserSettings(
                        Long userId) {

                return userSettingsRepository
                                .findByUserId(userId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Configuración del usuario no encontrada"));
        }
}
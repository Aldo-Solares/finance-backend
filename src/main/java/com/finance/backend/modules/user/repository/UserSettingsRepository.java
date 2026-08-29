package com.finance.backend.modules.user.repository;

import com.finance.backend.modules.user.model.UserSettings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository
        extends JpaRepository<UserSettings, Long> {

    Optional<UserSettings> findByUserId(
            Long userId);

    boolean existsByUserId(
            Long userId);
}
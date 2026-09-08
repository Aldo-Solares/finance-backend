package com.finance.backend.modules.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_settings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_settings_user_id", columnNames = "user_id")
})
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_settings_id")
    private Long userSettingsId;

    // ===================
    // USUARIO
    // ===================

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ===================
    // NOTIFICACIONES
    // ===================

    @Column(name = "statement_cutoff_reminder", nullable = false)
    private boolean statementCutoffReminder = false;

    // ===================
    // PERFIL
    // ===================

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_image_background", nullable = false, length = 20)
    private ProfileImageBackground profileImageBackground = ProfileImageBackground.BLUE;

    @Column(name = "use_profile_image_background_as_primary_color", nullable = false)
    private boolean useProfileImageBackgroundAsPrimaryColor = false;

    // ===================
    // CONSTRUCTOR
    // ===================

    public UserSettings() {
    }

    // ===================
    // GETTERS Y SETTERS
    // ===================

    public Long getUserSettingsId() {
        return userSettingsId;
    }

    public void setUserSettingsId(Long userSettingsId) {
        this.userSettingsId = userSettingsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean getStatementCutoffReminder() {
        return statementCutoffReminder;
    }

    public void setStatementCutoffReminder(boolean statementCutoffReminder) {
        this.statementCutoffReminder = statementCutoffReminder;
    }

    public ProfileImageBackground getProfileImageBackground() {
        return profileImageBackground;
    }

    public void setProfileImageBackground(ProfileImageBackground profileImageBackground) {
        this.profileImageBackground = profileImageBackground;
    }

    public boolean getUseProfileImageBackgroundAsPrimaryColor() {
        return useProfileImageBackgroundAsPrimaryColor;
    }

    public void setUseProfileImageBackgroundAsPrimaryColor(
            boolean useProfileImageBackgroundAsPrimaryColor) {
        this.useProfileImageBackgroundAsPrimaryColor = useProfileImageBackgroundAsPrimaryColor;
    }
}
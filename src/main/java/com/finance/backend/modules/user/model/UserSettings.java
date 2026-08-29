package com.finance.backend.modules.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_settings_user_id", columnNames = "user_id")
})
public class UserSettings {

    // ===================
    // IDENTIFICACIÓN
    // ===================

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

    public void setStatementCutoffReminder(
            boolean statementCutoffReminder) {

        this.statementCutoffReminder = statementCutoffReminder;
    }
}
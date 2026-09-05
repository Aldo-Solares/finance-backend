package com.finance.backend.modules.user.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    // ===================
    // IDENTIFICACIÓN
    // ===================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // ===================
    // INFORMACIÓN PERSONAL
    // ===================

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "second_last_name", length = 100)
    private String secondLastName;

    // ===================
    // CREDENCIALES
    // ===================

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    // ===================
    // AUTORIZACIÓN
    // ===================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // ===================
    // IMAGEN DE PERFIL
    // ===================
    @ManyToOne
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    // ===================
    // VERIFICACIÓN DE EMAIL
    // ===================

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "email_verification_token_hash", length = 255)
    private String emailVerificationTokenHash;

    @Column(name = "email_verification_token_expires_at")
    private LocalDateTime emailVerificationTokenExpiresAt;

    // ===================
    // RECUPERACIÓN DE PASSWORD
    // ===================

    @Column(name = "password_reset_token_hash", length = 255)
    private String passwordResetTokenHash;

    @Column(name = "password_reset_token_expires_at")
    private LocalDateTime passwordResetTokenExpiresAt;

    // ===================
    // CONSTRUCTOR
    // ===================

    public User() {
    }

    // ===================
    // GETTERS Y SETTERS
    // ===================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(
            ProfileImage profileImage) {

        this.profileImage = profileImage;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(
            boolean emailVerified) {

        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationTokenHash() {
        return emailVerificationTokenHash;
    }

    public void setEmailVerificationTokenHash(
            String emailVerificationTokenHash) {

        this.emailVerificationTokenHash = emailVerificationTokenHash;
    }

    public LocalDateTime getEmailVerificationTokenExpiresAt() {
        return emailVerificationTokenExpiresAt;
    }

    public void setEmailVerificationTokenExpiresAt(
            LocalDateTime expiresAt) {

        this.emailVerificationTokenExpiresAt = expiresAt;
    }

    public String getPasswordResetTokenHash() {
        return passwordResetTokenHash;
    }

    public void setPasswordResetTokenHash(
            String passwordResetTokenHash) {

        this.passwordResetTokenHash = passwordResetTokenHash;
    }

    public LocalDateTime getPasswordResetTokenExpiresAt() {
        return passwordResetTokenExpiresAt;
    }

    public void setPasswordResetTokenExpiresAt(
            LocalDateTime expiresAt) {

        this.passwordResetTokenExpiresAt = expiresAt;
    }
}
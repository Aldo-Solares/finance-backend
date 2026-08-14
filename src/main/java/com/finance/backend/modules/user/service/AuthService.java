package com.finance.backend.modules.user.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;

import com.finance.backend.modules.user.dto.auth.ForgotPasswordRequest;
import com.finance.backend.modules.user.dto.auth.LoginRequest;
import com.finance.backend.modules.user.dto.auth.LoginResponse;
import com.finance.backend.modules.user.dto.auth.RegisterRequest;
import com.finance.backend.modules.user.dto.auth.RegisterResponse;
import com.finance.backend.modules.user.dto.auth.ResendVerificationRequest;
import com.finance.backend.modules.user.dto.auth.ResetPasswordRequest;
import com.finance.backend.modules.user.dto.auth.VerifyEmailRequest;

import com.finance.backend.modules.user.mapper.UserMapper;
import com.finance.backend.modules.user.model.Role;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;

import com.finance.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(
            RegisterRequest request) {

        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "El correo ya está registrado");
        }

        User user = new User();

        user.setName(
                request.name().trim());

        user.setLastName(
                normalize(request.lastName()));

        user.setSecondLastName(
                normalize(request.secondLastName()));

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(
                        request.password()));

        user.setRole(Role.DEBTOR);

        user.setEmailVerified(false);

        String verificationToken = generateSecureToken();

        user.setEmailVerificationToken(
                verificationToken);

        user.setEmailVerificationTokenExpiresAt(
                LocalDateTime.now()
                        .plusHours(24));

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                UserMapper.toResponse(savedUser),
                verificationToken);
    }

    public void verifyEmail(
            VerifyEmailRequest request) {

        User user = userRepository
                .findByEmailVerificationToken(
                        request.token())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Token de verificación inválido"));

        LocalDateTime expiresAt = user.getEmailVerificationTokenExpiresAt();

        if (expiresAt == null
                || expiresAt.isBefore(
                        LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "El token de verificación expiró");
        }

        user.setEmailVerified(true);

        user.setEmailVerificationToken(null);

        user.setEmailVerificationTokenExpiresAt(null);

        userRepository.save(user);
    }

    public String resendVerification(
            ResendVerificationRequest request) {

        String email = normalizeEmail(request.email());

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario no encontrado"));

        if (user.getEmailVerified()) {
            throw new ConflictException(
                    "El correo ya está verificado");
        }

        String verificationToken = generateSecureToken();

        user.setEmailVerificationToken(
                verificationToken);

        user.setEmailVerificationTokenExpiresAt(
                LocalDateTime.now()
                        .plusHours(24));

        userRepository.save(user);

        return verificationToken;
    }

    public LoginResponse login(
            LoginRequest request) {

        String email = normalizeEmail(request.email());

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuario no encontrado"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole());
    }

    public String forgotPassword(
            ForgotPasswordRequest request) {

        String email = normalizeEmail(request.email());

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (user == null
                || !user.getEmailVerified()) {

            return null;
        }

        String resetToken = generateSecureToken();

        user.setPasswordResetToken(
                resetToken);

        user.setPasswordResetTokenExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(30));

        userRepository.save(user);

        return resetToken;
    }

    public void resetPassword(
            ResetPasswordRequest request) {

        User user = userRepository
                .findByPasswordResetToken(
                        request.token())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Token de recuperación inválido"));

        LocalDateTime expiresAt = user.getPasswordResetTokenExpiresAt();

        if (expiresAt == null
                || expiresAt.isBefore(
                        LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "El token de recuperación expiró");
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()));

        user.setPasswordResetToken(null);

        user.setPasswordResetTokenExpiresAt(null);

        userRepository.save(user);
    }

    // ===================
    // HELPERS
    // ===================
    private String generateSecureToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String normalizeEmail(
            String email) {

        return email
                .trim()
                .toLowerCase();
    }

    private String normalize(
            String value) {

        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
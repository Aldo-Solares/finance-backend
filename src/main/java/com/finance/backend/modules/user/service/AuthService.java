package com.finance.backend.modules.user.service;

import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ForbiddenException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.exception.UnauthorizedException;

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
import com.finance.backend.utils.email.UserEmailService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@Transactional
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final CustomUserDetailsService customUserDetailsService;
        private final UserEmailService userEmailService;

        private final SecureRandom secureRandom = new SecureRandom();

        public AuthService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        CustomUserDetailsService customUserDetailsService,
                        UserEmailService userEmailService) {

                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.customUserDetailsService = customUserDetailsService;
                this.userEmailService = userEmailService;
        }

        // ===================
        // REGISTRO
        // ===================

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

                user.setRole(Role.USER);
                user.setEmailVerified(false);

                String verificationToken = generateSecureToken();

                user.setEmailVerificationTokenHash(
                                hashToken(verificationToken));

                user.setEmailVerificationTokenExpiresAt(
                                LocalDateTime.now()
                                                .plusHours(24));

                User savedUser = userRepository.save(user);

                userEmailService.sendVerificationEmail(
                                savedUser.getEmail(),
                                verificationToken);

                return new RegisterResponse(
                                UserMapper.toResponse(savedUser));
        }

        // ===================
        // VERIFICACIÓN DE EMAIL
        // ===================

        public void verifyEmail(
                        VerifyEmailRequest request) {

                String tokenHash = hashToken(request.token());

                User user = userRepository
                                .findByEmailVerificationTokenHash(tokenHash)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Token de verificación inválido"));

                LocalDateTime expiresAt = user.getEmailVerificationTokenExpiresAt();

                if (expiresAt == null
                                || expiresAt.isBefore(LocalDateTime.now())) {

                        throw new BadRequestException(
                                        "El token de verificación expiró");
                }

                user.setEmailVerified(true);
                user.setEmailVerificationTokenHash(null);
                user.setEmailVerificationTokenExpiresAt(null);

                userRepository.save(user);
        }

        // ===================
        // REENVÍO DE VERIFICACIÓN
        // ===================

        public void resendVerification(
                        ResendVerificationRequest request) {

                String email = normalizeEmail(request.email());

                User user = userRepository
                                .findByEmailIgnoreCase(email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));

                if (Boolean.TRUE.equals(
                                user.getEmailVerified())) {

                        throw new ConflictException(
                                        "El correo ya está verificado");
                }

                String verificationToken = generateSecureToken();

                user.setEmailVerificationTokenHash(
                                hashToken(verificationToken));

                user.setEmailVerificationTokenExpiresAt(
                                LocalDateTime.now()
                                                .plusHours(24));

                userRepository.save(user);

                userEmailService.sendVerificationEmail(
                                user.getEmail(),
                                verificationToken);
        }

        // ===================
        // LOGIN
        // ===================

        @Transactional(readOnly = true)
        public LoginResponse login(
                        LoginRequest request) {

                String email = normalizeEmail(request.email());

                User user = userRepository
                                .findByEmailIgnoreCase(email)
                                .orElseThrow(
                                                () -> new UnauthorizedException(
                                                                "Correo o contraseña incorrectos"));

                if (!passwordEncoder.matches(
                                request.password(),
                                user.getPassword())) {

                        throw new UnauthorizedException(
                                        "Correo o contraseña incorrectos");
                }

                if (!Boolean.TRUE.equals(
                                user.getEmailVerified())) {

                        throw new ForbiddenException(
                                        "El correo no ha sido verificado");
                }

                UserDetails userDetails = customUserDetailsService
                                .loadUserByUsername(
                                                user.getEmail());

                String token = jwtService.generateToken(userDetails);

                return new LoginResponse(
                                token,
                                UserMapper.toResponse(user));
        }

        // ===================
        // RECUPERACIÓN DE PASSWORD
        // ===================

        public void forgotPassword(
                        ForgotPasswordRequest request) {

                String email = normalizeEmail(request.email());

                User user = userRepository
                                .findByEmailIgnoreCase(email)
                                .orElse(null);

                if (user == null
                                || !Boolean.TRUE.equals(
                                                user.getEmailVerified())) {

                        return;
                }

                String resetToken = generateSecureToken();

                user.setPasswordResetTokenHash(
                                hashToken(resetToken));

                user.setPasswordResetTokenExpiresAt(
                                LocalDateTime.now()
                                                .plusMinutes(30));

                userRepository.save(user);

                userEmailService.sendPasswordResetEmail(
                                user.getEmail(),
                                resetToken);
        }

        // ===================
        // RESET DE PASSWORD
        // ===================

        public void resetPassword(
                        ResetPasswordRequest request) {

                String tokenHash = hashToken(request.token());

                User user = userRepository
                                .findByPasswordResetTokenHash(tokenHash)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Token de recuperación inválido"));

                LocalDateTime expiresAt = user.getPasswordResetTokenExpiresAt();

                if (expiresAt == null
                                || expiresAt.isBefore(LocalDateTime.now())) {

                        throw new BadRequestException(
                                        "El token de recuperación expiró");
                }

                user.setPassword(
                                passwordEncoder.encode(
                                                request.newPassword()));

                user.setPasswordResetTokenHash(null);
                user.setPasswordResetTokenExpiresAt(null);

                userRepository.save(user);
        }

        // ===================
        // GENERACIÓN DE TOKEN
        // ===================

        private String generateSecureToken() {

                byte[] bytes = new byte[32];

                secureRandom.nextBytes(bytes);

                return Base64
                                .getUrlEncoder()
                                .withoutPadding()
                                .encodeToString(bytes);
        }

        // ===================
        // HASH DE TOKEN
        // ===================

        private String hashToken(
                        String token) {

                try {

                        MessageDigest digest = MessageDigest.getInstance(
                                        "SHA-256");

                        byte[] hash = digest.digest(
                                        token.getBytes(
                                                        StandardCharsets.UTF_8));

                        return HexFormat.of()
                                        .formatHex(hash);

                } catch (NoSuchAlgorithmException exception) {

                        throw new IllegalStateException(
                                        "SHA-256 no está disponible",
                                        exception);
                }
        }

        // ===================
        // NORMALIZACIÓN
        // ===================

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
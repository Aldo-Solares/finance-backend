package com.finance.backend.modules.user.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.user.dto.auth.ForgotPasswordRequest;
import com.finance.backend.modules.user.dto.auth.LoginRequest;
import com.finance.backend.modules.user.dto.auth.LoginResponse;
import com.finance.backend.modules.user.dto.auth.RegisterRequest;
import com.finance.backend.modules.user.dto.auth.RegisterResponse;
import com.finance.backend.modules.user.dto.auth.ResendVerificationRequest;
import com.finance.backend.modules.user.dto.auth.ResetPasswordRequest;
import com.finance.backend.modules.user.dto.auth.VerifyEmailRequest;
import com.finance.backend.modules.user.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthService authService;

        public AuthController(
                        AuthService authService) {

                this.authService = authService;
        }

        @PostMapping("/register")
        public ApiResponse<RegisterResponse> register(
                        @Valid @RequestBody RegisterRequest request) {

                RegisterResponse response = authService.register(request);

                return ApiResponse.success(
                                "Usuario registrado. Verifica tu correo.",
                                response);
        }

        @PostMapping("/verify-email")
        public ApiResponse<Void> verifyEmail(
                        @Valid @RequestBody VerifyEmailRequest request) {

                authService.verifyEmail(request);

                return ApiResponse.success(
                                "Correo verificado",
                                null);
        }

        @PostMapping("/resend-verification")
        public ApiResponse<Void> resendVerification(
                        @Valid @RequestBody ResendVerificationRequest request) {

                authService.resendVerification(request);

                return ApiResponse.success(
                                "Correo de verificación enviado",
                                null);
        }

        @PostMapping("/login")
        public ApiResponse<LoginResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                LoginResponse response = authService.login(request);

                return ApiResponse.success(
                                "Login correcto",
                                response);
        }

        @PostMapping("/forgot-password")
        public ApiResponse<Void> forgotPassword(
                        @Valid @RequestBody ForgotPasswordRequest request) {

                authService.forgotPassword(request);

                return ApiResponse.success(
                                "Si el correo existe, recibirás instrucciones para restablecer tu contraseña.",
                                null);
        }

        @PostMapping("/reset-password")
        public ApiResponse<Void> resetPassword(
                        @Valid @RequestBody ResetPasswordRequest request) {

                authService.resetPassword(request);

                return ApiResponse.success(
                                "Contraseña actualizada",
                                null);
        }
}
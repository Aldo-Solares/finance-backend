package com.finance.backend.utils.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserEmailService {

        private final EmailService emailService;
        private final String frontendUrl;

        public UserEmailService(
                        EmailService emailService,
                        @Value("${app.frontend-url}") String frontendUrl) {

                this.emailService = emailService;
                this.frontendUrl = frontendUrl;
        }

        // ===================
        // VERIFICACIÓN DE EMAIL
        // ===================

        public void sendVerificationEmail(
                        String email,
                        String token) {

                String actionUrl = frontendUrl
                                + "/auth/verify-email?token="
                                + token;

                emailService.sendActionEmail(
                                email,
                                "Verifica tu correo en Isha",
                                "Verifica tu correo",
                                "Confirma tu dirección de correo para activar tu cuenta y comenzar a usar Isha.",
                                "Verificar correo",
                                actionUrl);
        }

        // ===================
        // RESET DE PASSWORD
        // ===================

        public void sendPasswordResetEmail(
                        String email,
                        String token) {

                String actionUrl = frontendUrl
                                + "/auth/reset-password?token="
                                + token;

                emailService.sendActionEmail(
                                email,
                                "Restablece tu contraseña de Isha",
                                "Restablece tu contraseña",
                                "Recibimos una solicitud para cambiar tu contraseña. Usa el siguiente enlace para crear una nueva.",
                                "Cambiar contraseña",
                                actionUrl);
        }
}
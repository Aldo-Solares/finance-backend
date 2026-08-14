package com.finance.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.backend.dto.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final ObjectMapper objectMapper;

        public SecurityConfig() {
                this.objectMapper = new ObjectMapper();
        }

        // ===================
        // SECURITY FILTER CHAIN
        // ===================

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        JwtFilter jwtFilter,
                        RateLimitFilter rateLimitFilter)
                        throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())

                                .cors(cors -> {
                                })

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .exceptionHandling(exception -> exception

                                                .authenticationEntryPoint(
                                                                (request,
                                                                                response,
                                                                                authException) -> {

                                                                        writeErrorResponse(
                                                                                        response,
                                                                                        HttpServletResponse.SC_UNAUTHORIZED,
                                                                                        "Authentication required");
                                                                })

                                                .accessDeniedHandler(
                                                                (request,
                                                                                response,
                                                                                accessDeniedException) -> {

                                                                        writeErrorResponse(
                                                                                        response,
                                                                                        HttpServletResponse.SC_FORBIDDEN,
                                                                                        "Access denied");
                                                                }))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**")
                                                .permitAll()

                                                .anyRequest()
                                                .authenticated())

                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .addFilterAfter(
                                                rateLimitFilter,
                                                JwtFilter.class)

                                .build();
        }

        // ===================
        // PASSWORD ENCODER
        // ===================

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // ===================
        // ERROR RESPONSE
        // ===================

        private void writeErrorResponse(
                        HttpServletResponse response,
                        int status,
                        String message)
                        throws IOException {

                response.setStatus(status);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                objectMapper.writeValue(
                                response.getWriter(),
                                ApiResponse.error(message));
        }
}
package com.finance.backend.modules.user.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.exception.UnauthorizedException;
import com.finance.backend.modules.user.dto.user.ChangePasswordRequest;
import com.finance.backend.modules.user.dto.user.ChangeRoleRequest;
import com.finance.backend.modules.user.dto.user.UpdateUserRequest;
import com.finance.backend.modules.user.dto.user.UpdateUserResponse;
import com.finance.backend.modules.user.dto.user.UserResponse;
import com.finance.backend.modules.user.mapper.UserMapper;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;
import com.finance.backend.security.JwtService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final CustomUserDetailsService userDetailsService;

        public UserService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        CustomUserDetailsService userDetailsService) {

                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
        }
        // ===================
        // CONSULTAS
        // ===================

        public List<UserResponse> findAll() {

                return userRepository
                                .findAll()
                                .stream()
                                .map(UserMapper::toResponse)
                                .toList();
        }

        public UserResponse findById(
                        Long userId) {

                return UserMapper.toResponse(
                                getUserById(userId));
        }

        @Transactional(readOnly = true)
        public UserResponse findCurrentUser(
                        String email) {

                User user = userRepository
                                .findByEmailIgnoreCase(email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));

                return UserMapper.toResponse(user);
        }

        // ===================
        // ACTUALIZACIÓN DE PERFIL
        // ===================

        @Transactional
        public UpdateUserResponse updateMe(
                        String email,
                        UpdateUserRequest request) {

                User currentUser = getUserByEmail(email);

                String newEmail = request.email()
                                .trim()
                                .toLowerCase();

                boolean emailChanged = !currentUser
                                .getEmail()
                                .equalsIgnoreCase(newEmail);

                if (emailChanged) {

                        boolean emailExists = userRepository
                                        .existsByEmailIgnoreCaseAndUserIdNot(
                                                        newEmail,
                                                        currentUser.getUserId());

                        if (emailExists) {
                                throw new ConflictException(
                                                "El correo ya está registrado");
                        }

                        currentUser.setEmailVerified(false);

                        currentUser.setEmailVerificationTokenHash(null);
                        currentUser.setEmailVerificationTokenExpiresAt(null);
                }

                UserMapper.updateEntity(
                                currentUser,
                                request);

                User savedUser = userRepository.save(currentUser);

                UserDetails userDetails = userDetailsService.loadUserByUsername(
                                savedUser.getEmail());

                String token = jwtService.generateToken(userDetails);

                return new UpdateUserResponse(
                                UserMapper.toResponse(savedUser),
                                token);
        }

        // ===================
        // CAMBIO DE CONTRASEÑA
        // ===================

        public void changePassword(
                        String email,
                        ChangePasswordRequest request) {

                User currentUser = getUserByEmail(email);

                boolean matches = passwordEncoder.matches(
                                request.currentPassword(),
                                currentUser.getPassword());

                if (!matches) {
                        throw new UnauthorizedException(
                                        "La contraseña actual es incorrecta");
                }

                currentUser.setPassword(
                                passwordEncoder.encode(
                                                request.newPassword()));

                currentUser.setPasswordResetTokenHash(null);
                currentUser.setPasswordResetTokenExpiresAt(null);

                userRepository.save(currentUser);
        }

        // ===================
        // CAMBIO DE ROL
        // ===================

        public UserResponse changeRole(
                        Long userId,
                        ChangeRoleRequest request) {

                User user = getUserById(userId);

                user.setRole(
                                request.role());

                return UserMapper.toResponse(
                                userRepository.save(user));
        }

        // ===================
        // ELIMINACIÓN
        // ===================

        public void delete(
                        Long userId) {

                User user = getUserById(userId);

                userRepository.delete(user);
        }

        // ===================
        // HELPERS
        // ===================

        private User getUserById(
                        Long userId) {

                return userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }

        private User getUserByEmail(
                        String email) {

                return userRepository
                                .findByEmailIgnoreCase(email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }
}
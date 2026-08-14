package com.finance.backend.modules.user.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.user.dto.user.ChangePasswordRequest;
import com.finance.backend.modules.user.dto.user.ChangeRoleRequest;
import com.finance.backend.modules.user.dto.user.UpdateUserRequest;
import com.finance.backend.modules.user.dto.user.UserResponse;
import com.finance.backend.modules.user.mapper.UserMapper;
import com.finance.backend.modules.user.model.User;
import com.finance.backend.modules.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public UserService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {

                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

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

        public UserResponse getMe(
                        String email) {

                User user = getUserByEmail(email);

                return UserMapper.toResponse(user);
        }

        public UserResponse updateMe(
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
                }

                UserMapper.updateEntity(
                                currentUser,
                                request);

                User savedUser = userRepository.save(currentUser);

                return UserMapper.toResponse(savedUser);
        }

        public void changePassword(
                        String email,
                        ChangePasswordRequest request) {

                User currentUser = getUserByEmail(email);

                boolean matches = passwordEncoder.matches(
                                request.currentPassword(),
                                currentUser.getPassword());

                if (!matches) {
                        throw new IllegalArgumentException(
                                        "La contraseña actual es incorrecta");
                }

                currentUser.setPassword(
                                passwordEncoder.encode(
                                                request.newPassword()));

                userRepository.save(currentUser);
        }

        public UserResponse changeRole(
                        Long userId,
                        ChangeRoleRequest request) {

                User user = getUserById(userId);

                user.setRole(
                                request.role());

                return UserMapper.toResponse(
                                userRepository.save(user));
        }

        public void delete(
                        Long userId) {

                User user = getUserById(userId);

                userRepository.delete(user);
        }

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
                                .findByEmailIgnoreCase(
                                                email)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Usuario no encontrado"));
        }
}
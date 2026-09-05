package com.finance.backend.modules.user.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.user.dto.user.ChangePasswordRequest;
import com.finance.backend.modules.user.dto.user.ChangeRoleRequest;
import com.finance.backend.modules.user.dto.user.UpdateUserRequest;
import com.finance.backend.modules.user.dto.user.UpdateUserResponse;
import com.finance.backend.modules.user.dto.user.UserResponse;
import com.finance.backend.modules.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

        private final UserService userService;

        public UserController(
                        UserService userService) {

                this.userService = userService;
        }

        @GetMapping("/me")
        public ApiResponse<UserResponse> findCurrentUser(
                        Authentication authentication) {

                UserResponse response = userService.findCurrentUser(
                                authentication.getName());

                return ApiResponse.success(response);
        }

        @PutMapping("/me")
        public ApiResponse<UpdateUserResponse> updateMe(
                        Authentication authentication,
                        @Valid @RequestBody UpdateUserRequest request) {

                return ApiResponse.success(
                                "Usuario actualizado",
                                userService.updateMe(
                                                authentication.getName(),
                                                request));
        }

        @PatchMapping("/me/profile-image/{profileImageId}")
        public ApiResponse<UserResponse> updateProfileImage(
                        Authentication authentication,
                        @PathVariable Long profileImageId) {

                return ApiResponse.success(
                                "Imagen de perfil actualizada",
                                userService.updateProfileImage(
                                                authentication.getName(),
                                                profileImageId));
        }

        @PatchMapping("/me/password")
        public ApiResponse<Void> changePassword(
                        Authentication authentication,
                        @Valid @RequestBody ChangePasswordRequest request) {

                userService.changePassword(
                                authentication.getName(),
                                request);

                return ApiResponse.success(
                                "Contraseña actualizada",
                                null);
        }

        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<List<UserResponse>> findAll() {

                return ApiResponse.success(
                                userService.findAll());
        }

        @GetMapping("/{userId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<UserResponse> findById(
                        @PathVariable Long userId) {

                return ApiResponse.success(
                                userService.findById(userId));
        }

        @PatchMapping("/{userId}/role")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<UserResponse> changeRole(
                        @PathVariable Long userId,
                        @Valid @RequestBody ChangeRoleRequest request) {

                return ApiResponse.success(
                                "Rol actualizado",
                                userService.changeRole(
                                                userId,
                                                request));
        }

        @DeleteMapping("/{userId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<Void> delete(
                        @PathVariable Long userId) {

                userService.delete(userId);

                return ApiResponse.success(
                                "Usuario eliminado",
                                null);
        }

        @DeleteMapping("/me/profile-image")
        public ApiResponse<Void> removeProfileImage(
                        Authentication authentication) {

                userService.removeProfileImage(authentication.getName());

                return ApiResponse.success(
                                "Imagen de perfil eliminada",
                                null);
        }
}
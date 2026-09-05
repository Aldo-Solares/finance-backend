package com.finance.backend.modules.user.controller;

import com.finance.backend.dto.ApiResponse;
import com.finance.backend.modules.user.dto.profileimage.ProfileImageResponse;
import com.finance.backend.modules.user.dto.profileimage.UpdateProfileImageRequest;
import com.finance.backend.modules.user.dto.profileimage.UpdateProfileImageStatusRequest;
import com.finance.backend.modules.user.service.ProfileImageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile-images")
@Validated
public class ProfileImageController {

        private final ProfileImageService profileImageService;

        public ProfileImageController(
                        ProfileImageService profileImageService) {

                this.profileImageService = profileImageService;
        }

        @GetMapping
        public ApiResponse<List<ProfileImageResponse>> findActive() {

                return ApiResponse.success(
                                profileImageService.findActive());
        }

        @GetMapping("/admin")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<List<ProfileImageResponse>> findAll() {

                return ApiResponse.success(
                                profileImageService.findAll());
        }

        @PostMapping(consumes = "multipart/form-data")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<ProfileImageResponse> create(
                        @RequestParam @NotBlank @Size(max = 100) String name,
                        @RequestPart("file") MultipartFile file) {

                return ApiResponse.success(
                                "Imagen de perfil creada",
                                profileImageService.create(
                                                name,
                                                file));
        }

        @PatchMapping("/{profileImageId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<ProfileImageResponse> update(
                        @PathVariable Long profileImageId,
                        @Valid @RequestBody UpdateProfileImageRequest request) {

                return ApiResponse.success(
                                "Nombre de la imagen de perfil actualizado",
                                profileImageService.update(
                                                profileImageId,
                                                request));
        }

        @PatchMapping("/{profileImageId}/active")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<ProfileImageResponse> updateActive(
                        @PathVariable Long profileImageId,
                        @Valid @RequestBody UpdateProfileImageStatusRequest request) {

                return ApiResponse.success(
                                "Estado de la imagen de perfil actualizado",
                                profileImageService.updateActive(
                                                profileImageId,
                                                request));
        }

        @DeleteMapping("/{profileImageId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ApiResponse<Void> delete(
                        @PathVariable Long profileImageId) {

                profileImageService.delete(
                                profileImageId);

                return ApiResponse.success(
                                "Imagen de perfil eliminada",
                                null);
        }
}
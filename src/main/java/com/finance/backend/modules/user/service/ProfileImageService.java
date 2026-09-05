package com.finance.backend.modules.user.service;

import com.finance.backend.exception.ConflictException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.modules.user.dto.profileimage.ProfileImageResponse;
import com.finance.backend.modules.user.dto.profileimage.UpdateProfileImageRequest;
import com.finance.backend.modules.user.dto.profileimage.UpdateProfileImageStatusRequest;
import com.finance.backend.modules.user.mapper.ProfileImageMapper;
import com.finance.backend.modules.user.model.ProfileImage;
import com.finance.backend.modules.user.repository.ProfileImageRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProfileImageService {

        private static final int MAX_PROFILE_IMAGES = 20;
        private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

        private final ProfileImageRepository profileImageRepository;
        private final Path storageDirectory;
        private final String backendUrl;

        public ProfileImageService(
                        ProfileImageRepository profileImageRepository,
                        @Value("${app.storage.profile-image-directory:uploads/profile-images}") String storageDirectory,
                        @Value("${app.backend-url:http://localhost:9000}") String backendUrl) {

                this.profileImageRepository = profileImageRepository;

                this.storageDirectory = Paths.get(storageDirectory)
                                .toAbsolutePath()
                                .normalize();

                this.backendUrl = backendUrl.replaceAll("/$", "");

                createStorageDirectory();
        }

        @Transactional(readOnly = true)
        public List<ProfileImageResponse> findActive() {

                return profileImageRepository
                                .findByActiveTrueOrderByProfileImageIdAsc()
                                .stream()
                                .map(profileImage -> ProfileImageMapper.toResponse(
                                                profileImage,
                                                backendUrl))
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<ProfileImageResponse> findAll() {

                return profileImageRepository
                                .findAll()
                                .stream()
                                .map(profileImage -> ProfileImageMapper.toResponse(
                                                profileImage,
                                                backendUrl))
                                .toList();
        }

        @Transactional
        public ProfileImageResponse create(
                        String name,
                        MultipartFile file) {

                validateFile(file);

                if (profileImageRepository.count() >= MAX_PROFILE_IMAGES) {
                        throw new ConflictException(
                                        "El catálogo no puede tener más de 20 imágenes");
                }

                String normalizedName = name == null
                                ? ""
                                : name.trim();

                if (normalizedName.isBlank()) {
                        throw new IllegalArgumentException(
                                        "El nombre de la imagen es obligatorio");
                }

                if (profileImageRepository.existsByNameIgnoreCase(normalizedName)) {
                        throw new ConflictException(
                                        "Ya existe una imagen con ese nombre");
                }

                String extension = getExtension(file);

                String fileName = UUID.randomUUID()
                                + "."
                                + extension;

                Path target = storageDirectory
                                .resolve(fileName)
                                .normalize();

                if (!target.startsWith(storageDirectory)) {
                        throw new IllegalArgumentException(
                                        "Ruta de archivo inválida");
                }

                try {
                        Files.copy(
                                        file.getInputStream(),
                                        target);
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "No fue posible guardar la imagen",
                                        exception);
                }

                ProfileImage profileImage = new ProfileImage();

                profileImage.setName(normalizedName);
                profileImage.setFileName(fileName);
                profileImage.setActive(true);

                try {
                        ProfileImage savedProfileImage = profileImageRepository.save(profileImage);

                        return ProfileImageMapper.toResponse(
                                        savedProfileImage,
                                        backendUrl);

                } catch (RuntimeException exception) {
                        deleteFile(target);
                        throw exception;
                }
        }

        @Transactional
        public ProfileImageResponse updateActive(
                        Long profileImageId,
                        UpdateProfileImageStatusRequest request) {

                ProfileImage profileImage = getProfileImageById(profileImageId);

                profileImage.setActive(request.active());

                ProfileImage savedProfileImage = profileImageRepository.save(profileImage);

                return ProfileImageMapper.toResponse(
                                savedProfileImage,
                                backendUrl);
        }

        @Transactional
        public void delete(Long profileImageId) {

                ProfileImage profileImage = getProfileImageById(profileImageId);

                long usersUsingProfileImage = profileImageRepository.countUsersByProfileImageId(
                                profileImageId);

                /*
                 * Si la imagen está asignada a usuarios,
                 * no se elimina físicamente. Se desactiva.
                 */
                if (usersUsingProfileImage > 0) {
                        profileImage.setActive(false);
                        profileImageRepository.save(profileImage);
                        return;
                }

                profileImageRepository.delete(profileImage);

                Path file = storageDirectory
                                .resolve(profileImage.getFileName())
                                .normalize();

                if (!file.startsWith(storageDirectory)) {
                        throw new IllegalStateException(
                                        "Ruta de archivo inválida");
                }

                deleteFile(file);
        }

        @Transactional(readOnly = true)
        public ProfileImage getActiveProfileImageById(
                        Long profileImageId) {

                ProfileImage profileImage = getProfileImageById(profileImageId);

                if (!profileImage.getActive()) {
                        throw new ConflictException(
                                        "La imagen seleccionada no está disponible");
                }

                return profileImage;
        }

        private ProfileImage getProfileImageById(
                        Long profileImageId) {

                return profileImageRepository
                                .findById(profileImageId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Imagen de perfil no encontrada"));
        }

        private void validateFile(MultipartFile file) {

                if (file == null || file.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "La imagen es obligatoria");
                }

                if (file.getSize() > MAX_FILE_SIZE) {
                        throw new IllegalArgumentException(
                                        "La imagen no puede superar los 5 MB");
                }

                String extension = getExtension(file);

                if (!extension.equals("png")
                                && !extension.equals("jpg")
                                && !extension.equals("jpeg")
                                && !extension.equals("webp")) {

                        throw new IllegalArgumentException(
                                        "El formato debe ser PNG, JPG, JPEG o WebP");
                }

                String contentType = file.getContentType();

                if (contentType == null
                                || (!contentType.equalsIgnoreCase("image/png")
                                                && !contentType.equalsIgnoreCase("image/jpeg")
                                                && !contentType.equalsIgnoreCase("image/webp"))) {

                        throw new IllegalArgumentException(
                                        "El archivo no es una imagen válida");
                }
        }

        private String getExtension(MultipartFile file) {

                String originalFilename = file.getOriginalFilename();

                if (originalFilename == null
                                || !originalFilename.contains(".")) {

                        throw new IllegalArgumentException(
                                        "La imagen debe tener una extensión válida");
                }

                return originalFilename
                                .substring(
                                                originalFilename.lastIndexOf('.') + 1)
                                .toLowerCase(Locale.ROOT);
        }

        private void createStorageDirectory() {

                try {
                        Files.createDirectories(storageDirectory);
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "No fue posible crear el directorio de imágenes de perfil",
                                        exception);
                }
        }

        private void deleteFile(Path file) {

                try {
                        Files.deleteIfExists(file);
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "No fue posible eliminar la imagen",
                                        exception);
                }
        }

        @Transactional
        public ProfileImageResponse update(
                        Long profileImageId,
                        UpdateProfileImageRequest request) {

                ProfileImage profileImage = getProfileImageById(profileImageId);

                String normalizedName = request.name().trim();

                boolean nameChanged = !profileImage.getName()
                                .equalsIgnoreCase(normalizedName);

                if (nameChanged
                                && profileImageRepository.existsByNameIgnoreCase(
                                                normalizedName)) {

                        throw new ConflictException(
                                        "Ya existe una imagen con ese nombre");
                }

                profileImage.setName(normalizedName);

                ProfileImage savedProfileImage = profileImageRepository.save(profileImage);

                return ProfileImageMapper.toResponse(
                                savedProfileImage,
                                backendUrl);
        }

}
package com.finance.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class ProfileImageStorageConfig
                implements WebMvcConfigurer {

        private final String storageDirectory;

        public ProfileImageStorageConfig(
                        @Value("${APP_STORAGE_PROFILE_IMAGE_DIRECTORY") String storageDirectory) {

                this.storageDirectory = Paths.get(storageDirectory)
                                .toAbsolutePath()
                                .normalize()
                                .toString();
        }

        @Override
        public void addResourceHandlers(
                        ResourceHandlerRegistry registry) {

                registry
                                .addResourceHandler(
                                                "/uploads/profile-images/**")
                                .addResourceLocations(
                                                "file:" + storageDirectory + "/");
        }
}
package com.finance.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class ProfileImageStorageConfig
                implements WebMvcConfigurer {

        private final String storageLocation;

        public ProfileImageStorageConfig(
                        @Value("${app.storage.profile-image-directory}") String storageDirectory) {

                String resourceLocation = Paths.get(storageDirectory)
                                .toAbsolutePath()
                                .normalize()
                                .toUri()
                                .toString();

                this.storageLocation = resourceLocation.endsWith("/")
                                ? resourceLocation
                                : resourceLocation + "/";
        }

        @Override
        public void addResourceHandlers(
                        ResourceHandlerRegistry registry) {

                registry
                                .addResourceHandler(
                                                "/uploads/profile-images/**")
                                .addResourceLocations(
                                                storageLocation);
        }
}

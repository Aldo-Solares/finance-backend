package com.finance.backend.modules.user.mapper;

import com.finance.backend.modules.user.dto.profileimage.ProfileImageResponse;
import com.finance.backend.modules.user.model.ProfileImage;

public final class ProfileImageMapper {

    private static final String IMAGE_URL_PREFIX = "/uploads/profile-images/";

    private ProfileImageMapper() {
    }

    public static ProfileImageResponse toResponse(
            ProfileImage profileImage,
            String backendUrl) {

        return new ProfileImageResponse(
                profileImage.getProfileImageId(),
                profileImage.getName(),
                backendUrl + IMAGE_URL_PREFIX + profileImage.getFileName(),
                profileImage.getActive());
    }
}
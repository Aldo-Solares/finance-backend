package com.finance.backend.modules.user.dto.profileimage;

public record ProfileImageResponse(
        Long profileImageId,
        String name,
        String imageUrl,
        Boolean active) {
}
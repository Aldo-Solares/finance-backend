package com.finance.backend.modules.user.dto.profileimage;

public record ProfileImageResponse(
                Long id,
                String name,
                String imageUrl,
                Boolean active) {
}
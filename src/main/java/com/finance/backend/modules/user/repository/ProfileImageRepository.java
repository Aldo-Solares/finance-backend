package com.finance.backend.modules.user.repository;

import com.finance.backend.modules.user.model.ProfileImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfileImageRepository
                extends JpaRepository<ProfileImage, Long> {

        List<ProfileImage> findByActiveTrueOrderByProfileImageIdAsc();

        boolean existsByNameIgnoreCase(String name);

        @Query("""
                        select count(u)
                        from User u
                        where u.profileImage.profileImageId = :profileImageId
                        """)
        long countUsersByProfileImageId(
                        Long profileImageId);
}
package com.finance.backend.modules.user.repository;

import com.finance.backend.modules.user.model.ProfileImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfileImageRepository
                extends JpaRepository<ProfileImage, Long> {

        List<ProfileImage> findByActiveTrueOrderByNameAsc();

        boolean existsByNameIgnoreCase(String name);

        @Query("""
                        select count(u)
                        from User u
                        where u.profileImage.id = :id
                        """)
        long countUsersByProfileImageId(
                        Long profileImageId);
}
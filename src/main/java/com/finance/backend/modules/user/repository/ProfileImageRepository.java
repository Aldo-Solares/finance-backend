package com.finance.backend.modules.user.repository;

import com.finance.backend.modules.user.model.ProfileImage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileImageRepository
                extends JpaRepository<ProfileImage, Long> {

        List<ProfileImage> findAllByOrderByNameAsc();

        boolean existsByNameIgnoreCase(String name);
}
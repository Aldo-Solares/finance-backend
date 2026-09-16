package com.finance.backend.modules.user.repository;

import com.finance.backend.modules.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository
                extends JpaRepository<User, Long> {

        Optional<User> findByEmailIgnoreCase(
                        String email);

        boolean existsByEmailIgnoreCase(
                        String email);

        boolean existsByEmailIgnoreCaseAndUserIdNot(
                        String email,
                        Long userId);

        Optional<User> findByEmailVerificationTokenHash(
                        String tokenHash);

        Optional<User> findByPasswordResetTokenHash(
                        String tokenHash);

        @Modifying
        @Query("""
                        update User u
                        set u.profileImage = null
                        where u.profileImage.profileImageId = :profileImageId
                        """)
        int clearProfileImageReferences(
                        Long profileImageId);
}
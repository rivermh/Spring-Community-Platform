package com.community.platform.email.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.community.platform.email.entity.EmailVerificationToken;
import com.community.platform.user.entity.User;


public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    @Modifying
    @Query("delete from EmailVerificationToken e where e.user = :user")
    void deleteByUser(@Param("user") User user);

    Optional<EmailVerificationToken> findByUser(User user);
}

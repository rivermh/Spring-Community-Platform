package com.community.platform.email.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.platform.email.entity.EmailVerificationToken;
import com.community.platform.user.entity.User;



public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	Optional<EmailVerificationToken> findByToken(String token);
	
	Optional<EmailVerificationToken> findByUser(User user);
	void deleteByUser(User user);
}

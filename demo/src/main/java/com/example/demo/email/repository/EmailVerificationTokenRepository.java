package com.example.demo.email.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.email.entity.EmailVerificationToken;
import com.example.demo.user.entity.User;



public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	Optional<EmailVerificationToken> findByToken(String token);
	
	Optional<EmailVerificationToken> findByUser(User user);
	void deleteByUser(User user);
}

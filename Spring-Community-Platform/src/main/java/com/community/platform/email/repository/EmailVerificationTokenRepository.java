package com.community.platform.email.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.community.platform.email.entity.EmailVerificationToken;
import com.community.platform.user.entity.User;

import jakarta.transaction.Transactional;



public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	Optional<EmailVerificationToken> findByToken(String token);
	@Modifying // 데이터를 변경/삭제할 때 필수
    @Transactional // 삭제 작업을 별도 트랜잭션으로 보장
	void deleteByUser(User user);
	

	Optional<EmailVerificationToken> findByUser(User user);

}

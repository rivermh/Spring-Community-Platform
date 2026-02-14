package com.community.platform.email.service;

import org.springframework.stereotype.Service;

import com.community.platform.email.entity.EmailVerificationToken;
import com.community.platform.email.repository.EmailVerificationTokenRepository;
import com.community.platform.user.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService {

	private final EmailVerificationTokenRepository tokenRepository;

    // 토큰을 찾고 만료됐는지 검사 (공통 사용)
    public EmailVerificationToken validateToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 링크입니다."));

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken); // 만료된 건 지워줌
            throw new IllegalStateException("인증 링크가 만료되었습니다.");
        }
        return verificationToken;
    }

    // 유저 활성화 및 토큰 삭제 (회원가입 완료용)
    public void confirmVerification(String token) {
        EmailVerificationToken verificationToken = validateToken(token);
        User user = verificationToken.getUser();

        if (user.isEnabled()) {
            throw new IllegalStateException("이미 인증이 완료된 계정입니다.");
        }

        user.activate(); // 유저 활성화
        tokenRepository.delete(verificationToken); 
    }
}

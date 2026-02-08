package com.community.platform.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.community.platform.email.entity.EmailVerificationToken;
import com.community.platform.email.repository.EmailVerificationTokenRepository;
import com.community.platform.user.entity.User;
import com.community.platform.user.repository.UserRepository;
import com.community.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Transactional
public class EmailVerificationController {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/verify-email")
    public String verify(@RequestParam String token, Model model) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElse(null);

        // 1️ 토큰 없음
        if (verificationToken == null) {
            model.addAttribute("message", "유효하지 않은 인증 링크입니다.");
            return "email/verify-result";
        }

        // 2️ 토큰 만료
        if (verificationToken.isExpired()) {
            model.addAttribute("message", "인증 링크가 만료되었습니다.");
            return "email/verify-result";
        }

        User user = verificationToken.getUser();

        // 3️ 이미 인증된 계정
        if (user.isEnabled()) {
            model.addAttribute("message", "이미 인증이 완료된 계정입니다.");
            return "email/verify-result";
        }

        // 4️ 인증 처리
        user.activate();          // enabled = true
        
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        model.addAttribute("message", "이메일 인증이 완료되었습니다. 로그인해주세요.");
        return "email/verify-result";
    }
    
    @GetMapping("/resend-verification")
    public String resend(@RequestParam("username") String username, Model model) {
        try {
            userService.resendVerificationEmail(username);
            model.addAttribute("message", "인증 메일이 성공적으로 재발송되었습니다.");
        } catch (Exception e) {
            System.out.println("디버깅 에러: " + e.getMessage());
            model.addAttribute("message", "오류 발생: " + e.getMessage());
        }
        return "email/verify-result";
    }
}



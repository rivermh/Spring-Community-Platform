package com.community.platform.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.community.platform.email.service.EmailVerificationService;
import com.community.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    // 이메일 인증 링크 클릭 시 처리
    @GetMapping("/verify-email")
    public String verify(@RequestParam("token") String token, Model model) {
        try {
            // 검증 + 활성화 + 삭제를 한 번에 처리
            emailVerificationService.confirmVerification(token);
            model.addAttribute("message", "이메일 인증이 완료되었습니다. 로그인해주세요.");
        } catch (Exception e) {
            // 서비스에서 던진 예외 메시지(유효하지 않음, 만료됨 등)를 사용자에게 전달
            model.addAttribute("message", e.getMessage());
        }
        return "email/verify-result";
    }
    
    // 이메일 재전송 
    @GetMapping("/resend-verification")
    public String resend(@RequestParam("username") String username, Model model) {
        try {
            userService.resendVerificationEmail(username);
            model.addAttribute("message", "인증 메일이 성공적으로 재발송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("message", "오류 발생: " + e.getMessage());
        }
        return "email/verify-result";
    }
    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        try {
            // 1. 토큰이 유효한지 검사 (EmailVerificationService 활용)
            // 만료되었거나 없는 토큰이면 여기서 예외가 터져서 catch문
            emailVerificationService.validateToken(token);
            
            // 2. 유효하다면 HTML에 토큰을 전달하며 비번 변경 페이지 오픈
            model.addAttribute("token", token);
            return "account/reset-password-form"; // 
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "email/verify-result"; // 에러 시 결과 페이지로
        }
    }
}
package com.community.platform.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.community.platform.email.service.EmailVerificationService;
import com.community.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    /**
     * [이메일 인증 링크 클릭 시 처리]
     */
    @GetMapping("/verify-email")
    public String verify(@RequestParam("token") String token, Model model) {
        try {
            // 토큰 검증 및 계정 활성화
            emailVerificationService.confirmVerification(token);
            
            model.addAttribute("success", true);
            model.addAttribute("message", "이메일 인증이 성공적으로 완료되었습니다! 이제 모든 커뮤니티 활동을 시작하실 수 있습니다.");
        } catch (Exception e) {
            log.error("#### [인증 실패] 토큰: {}, 사유: {}", token, e.getMessage());
            
            model.addAttribute("success", false);
            model.addAttribute("message", "인증에 실패했습니다. 링크가 만료되었거나 이미 사용되었을 수 있습니다. 다시 시도해 주세요.");
        }
        return "email/verify-result";
    }

    /**
     * [인증 메일 재전송]
     */
    @GetMapping("/resend-verification")
    public String resend(@RequestParam("username") String username, Model model) {
        log.info("#### [재전송 요청 수신] 대상 아이디: {}", username);
        try {
            userService.resendVerificationEmail(username);
            
            model.addAttribute("success", true);
            model.addAttribute("message", "새로운 인증 메일을 보내드렸습니다. 메일함(또는 스팸함)을 확인해 주세요!");
        } catch (Exception e) {
            log.error("#### [재전송 실패] 아이디: {}, 원인: {}", username, e.getMessage());
            
            model.addAttribute("success", false);
            model.addAttribute("message", "메일 발송 중 문제가 발생했습니다: " + e.getMessage());
        }
        return "email/verify-result";
    }

    /**
     * [비밀번호 재설정 폼 요청]
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        try {
            // 토큰 유효성 선검사
            emailVerificationService.validateToken(token);
            
            model.addAttribute("token", token);
            return "account/reset-password-form";
        } catch (Exception e) {
            log.error("#### [비밀번호 재설정 접근 실패] 토큰: {}, 사유: {}", token, e.getMessage());
            
            model.addAttribute("success", false);
            model.addAttribute("message", "비밀번호 재설정 링크가 유효하지 않습니다. 다시 요청해 주세요.");
            return "email/verify-result";
        }
    }
}
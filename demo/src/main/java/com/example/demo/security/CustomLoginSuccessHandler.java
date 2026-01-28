package com.example.demo.security;

import com.example.demo.common.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 1. 유저 상태가 WITHDRAWN(탈퇴)인 경우
        if (userDetails.getUser().getStatus() == UserStatus.WITHDRAWN) {
            // 강제 로그아웃 처리 (세션 무효화)
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            
            // 로그인 페이지로 에러 메시지와 함께 리다이렉트
            // URL에 "탈퇴"라는 글자가 포함되어야 login.html의 th:if 조건이 작동합니다.
            String errorMessage = URLEncoder.encode("탈퇴한 계정입니다. 복구가 필요합니다.", StandardCharsets.UTF_8);
            String username = userDetails.getUsername();
            
            getRedirectStrategy().sendRedirect(request, response, 
                "/login?error=" + errorMessage + "&username=" + username);
            return;
        }

        // 2. 이메일 미인증 상태인 경우 (필요 시 추가)
        if (userDetails.getUser().getStatus() == UserStatus.INACTIVE) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            String errorMessage = URLEncoder.encode("이메일 인증이 필요합니다.", StandardCharsets.UTF_8);
            getRedirectStrategy().sendRedirect(request, response, 
                "/login?error=" + errorMessage + "&username=" + userDetails.getUsername());
            return;
        }

        // 일반 유저인 경우 원래 가려던 곳이나 메인으로 이동
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
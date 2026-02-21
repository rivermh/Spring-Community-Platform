package com.community.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.community.platform.common.UserStatus;
import com.community.platform.user.repository.UserRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final UserRepository userRepository;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {

		String errorMessage;
		String username = request.getParameter("username");

		// 1. 계정 비활성화(DisabledException) 발생 시 상세 체크
	    if (exception instanceof DisabledException && username != null) {
	        // DB를 직접 조회해서 상태를 확인합니다.
	        errorMessage = userRepository.findByUsername(username)
	            .map(user -> {
	                if (user.getStatus() == UserStatus.WITHDRAWN) {
	                    return "탈퇴 처리된 계정입니다. 계정 복구가 필요합니다.";
	                } else if (user.getStatus() == UserStatus.INACTIVE) {
	                    return "이메일 인증이 완료되지 않았습니다.";
	                }
	                return "비활성화된 계정입니다.";
	            }).orElse("비활성화된 계정입니다.");
	    }
		// 2. 계정 잠금 (isAccountNonLocked = false) -> BANNED 상태
		else if (exception instanceof LockedException) {
			errorMessage = "운영 정책 위반으로 인해 정지된 계정입니다. 고객센터에 문의하세요.";
		}
		// 3. 비밀번호 틀림
		else if (exception instanceof BadCredentialsException) {
			errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
		}
		// 4. 존재하지 않는 사용자 (또는 시스템 내부 오류)
		else if (exception instanceof InternalAuthenticationServiceException) {
			errorMessage = "존재하지 않는 계정입니다. 다시 확인해주세요.";
		}
		// 5. 그 외 예외
		else {
			errorMessage = "알 수 없는 이유로 로그인에 실패했습니다. 관리자에게 문의하세요.";
		}
	    
	    
		// UTF-8 인코딩 후 리다이렉트
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        String encodedUsername = URLEncoder.encode(username != null ? username : "", StandardCharsets.UTF_8);

        // username도 함께 넘겨줘야 HTML의 input 창에 자동
        response.sendRedirect("/login?error=" + encodedMessage + "&username=" + encodedUsername);
	}
}
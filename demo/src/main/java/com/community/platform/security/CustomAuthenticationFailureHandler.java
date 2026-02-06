package com.community.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(
	        HttpServletRequest request,
	        HttpServletResponse response,
	        AuthenticationException exception)
	        throws IOException {

	    String errorMessage = "로그인에 실패했습니다.";

	    // 1️ Service 단에서 던진 예외 (가장 중요)
	    if (exception instanceof InternalAuthenticationServiceException) {
	        Throwable cause = exception.getCause();
	        errorMessage = (cause != null)
	                ? cause.getMessage()
	                : exception.getMessage();
	    }
	    // 2️ 계정 비활성 / 탈퇴
	    else if (exception instanceof DisabledException) {
	        errorMessage = exception.getMessage();
	    }
	    // 3️ 계정 잠금
	    else if (exception instanceof LockedException) {
	        errorMessage = "계정이 잠겨 있습니다.";
	    }
	    // 4️ 비밀번호 오류
	    else if (exception instanceof BadCredentialsException) {
	        errorMessage = "비밀번호가 틀렸습니다.";
	    }

	    response.sendRedirect(
	            "/login?error=" +
	            java.net.URLEncoder.encode(errorMessage, "UTF-8")
	    );
	}
}


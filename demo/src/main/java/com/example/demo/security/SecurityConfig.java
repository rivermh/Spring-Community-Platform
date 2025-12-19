package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	
	//비밀번호 암호화기 (BCrypt)
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// Security 핵심 설정
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
			// 개발 단계에서는 CSRF 비활성화
			.csrf(csrf -> csrf.disable())
			
			
			// 요청 권한 설정
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(
							"/",
							"/login",
							"/register",
							"/css/**",
							"/js/**"
					).permitAll()
					.anyRequest().authenticated()
			)
			
			// 로그인 설정
			.formLogin(login -> login
					.loginPage("/login")
					.usernameParameter("username")
					.passwordParameter("password")
					.defaultSuccessUrl("/", true)
			)
			
			//로그아웃 설정
			.logout(logout -> logout
				.logoutSuccessUrl("/")
			)
			
			// 우리가 만든 인증 로직 사용
			.userDetailsService(customUserDetailsService);
		return http.build();
	}
	
}

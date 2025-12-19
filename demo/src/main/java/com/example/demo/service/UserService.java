package com.example.demo.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	// 회원가입
	public void register(String username, String password, String email) {
		// 1. 아이디 중복 체크
		if(userRepository.existsByUsername(username)) {
			throw new IllegalStateException("이미 존재하는 아이디 입니다.");
		}
		
		// 2. 이메일 중복 체크
		if(userRepository.existsByUsername(email)) {
			throw new IllegalStateException("이미 존재하는 이메일입니다.");
		}
		 
		// 3. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(password);
		
		// 4. User 생성
		User user = User.builder()
				.username(username)
				.password(encodedPassword)
				.email(email)
				.role(Role.USER) // 기본 권한
				.build();
		
		// 5. 저장
		userRepository.save(user);
	}
	
	// 조회 메서드 
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("사용자 없음"));
	}
}

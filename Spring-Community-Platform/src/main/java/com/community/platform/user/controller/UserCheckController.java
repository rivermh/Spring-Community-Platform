package com.community.platform.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserCheckController {

	private final UserService userService;
	
	//아이디 중복 체크
	@GetMapping("/users/check-username")
	public boolean checkUsername(@RequestParam String username) {
		if(username == null || username.trim().isEmpty()) {
			return false;
		} 
		return !userService.existsByUsername(username);
	}
	
	// 이메일 중복 체크
	@GetMapping("/users/check-email")
	public boolean checkEmail(@RequestParam String email) {
		if(email == null || email.trim().isEmpty()) {
			return false;
		}
		return !userService.existsByEmail(email);
	}
}

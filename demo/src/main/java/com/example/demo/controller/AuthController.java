package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	// 회원가입 페이지
	@GetMapping("/register")
	public String registerForm() {
		return "register";
	}

	// 회원가입 처리
	@PostMapping("/register")
	public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
		userService.register(username, password, email);
		return "redirect:/login";
	}
}

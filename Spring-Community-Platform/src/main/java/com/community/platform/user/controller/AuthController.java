package com.community.platform.user.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.community.platform.user.service.UserService;

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
	public String register(
	        @RequestParam String username,
	        @RequestParam String password,
	        @RequestParam String email,
	        @RequestParam LocalDate birth,
	        RedirectAttributes redirectAttributes
	) {
	    try {
	        userService.register(username, password, email, birth);

	        redirectAttributes.addFlashAttribute(
	            "message",
	            "입력하신 이메일로 인증 메일을 발송했습니다. 메일을 확인해주세요."
	        );

	        return "redirect:/register";

	    } catch (IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        return "redirect:/register";
	    }
	}
}
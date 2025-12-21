package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {

	private final UserService userService;

	@GetMapping("/mypage")
	public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		// 로그인 안 된 상태 방어
		if(userDetails == null) {
			return "redirect:/login";
		}
		
		User user = userService.findByUsername(userDetails.getUsername());
		model.addAttribute("user", user);
		
		return "mypage";
	}
}

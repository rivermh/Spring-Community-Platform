package com.community.platform.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.community.platform.user.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

	private final UserService userService;

	@GetMapping("/restore")
	public String restoreForm(@RequestParam(name = "username", required = false) String username, 
	                          Model model, 
	                          RedirectAttributes ra) {
	    
	    // 1. 아이디가 아예 입력되지 않은 경우
	    if (username == null || username.isBlank()) {
	        ra.addFlashAttribute("error", "복구할 아이디를 입력해주세요."); // 👈 문구 추가
	        return "redirect:/login";
	    }

	    // 2. 탈퇴한 유저가 아니거나 존재하지 않는 경우
	    if (!userService.isUserWithdrawn(username)) {
	        ra.addFlashAttribute("error", "해당 아이디는 복구 대상이 아닙니다."); // 👈 문구 추가
	        return "redirect:/login";
	    }

	    // 3. 정상적인 경우
	    model.addAttribute("username", username);
	    return "account/restore";
	}

	// 계정 복구(Post)
	@PostMapping("/restore")
	public String restore(@RequestParam String username, @RequestParam String password, RedirectAttributes ra) {

		try {
			userService.restoreUser(username, password);
			ra.addFlashAttribute("message", "계정이 복구되었습니다. 로그인해주세요.");
			return "redirect:/login";
		} catch (IllegalArgumentException e) {
			// 비밀번호가 틀리거나 사용자가 없는 경우
			ra.addFlashAttribute("error", e.getMessage());
			// 입력했던 username을 다시 유지하기 위해 리다이렉트 경로에 포함
			return "redirect:/account/restore?username=" + username;
		}
	}

	// 1. 비밀번호 찾기 페이지 (이메일 입력창)
	@GetMapping("/find-password")
	public String findPasswordForm() {
		return "account/find-password";
	}

	// 2. 비밀번호 재설정 이메일 발송 요청
	@PostMapping("/find-password")
	public String findPassword(@RequestParam String email, RedirectAttributes ra) {
		try {
			
			userService.sendPasswordResetLink(email);
			ra.addFlashAttribute("message", "입력하신 이메일로 비밀번호 재설정 링크를 발송했습니다.");
			return "redirect:/login"; // 또는 결과 페이지
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/account/find-password";
		}
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(
	        @RequestParam String token,
	        @RequestParam String newPassword,
	        @RequestParam String confirmPassword,
	        RedirectAttributes ra) {

	    if (!newPassword.equals(confirmPassword)) {
	        ra.addFlashAttribute("error", "비밀번호 확인이 일치하지 않습니다.");
	        // 실패 시 다시 폼으로 리다이렉트 (토큰 유지)
	        return "redirect:/reset-password?token=" + token; 
	    }

	    try {
	        userService.resetPasswordByToken(token, newPassword);
	        ra.addFlashAttribute("message", "비밀번호가 변경되었습니다. 새 비밀번호로 로그인해주세요.");
	        return "redirect:/login";
	    } catch (Exception e) {
	        ra.addFlashAttribute("error", e.getMessage());
	        return "redirect:/account/find-password";
	    }
	}
}

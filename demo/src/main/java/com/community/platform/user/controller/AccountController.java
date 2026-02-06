package com.community.platform.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.community.platform.user.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    // 복구 페이지
    @GetMapping("/restore")
    public String restoreForm() {
        return "account/restore";
    }

    @PostMapping("/restore")
    public String restore(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes ra) {

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
}

package com.community.platform.admin.controller;

import com.community.platform.admin.dto.AdminUserResponseDto;
import com.community.platform.admin.service.AdminPostService; // 추가
import com.community.platform.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMainController {

    private final AdminUserService adminUserService;
    private final AdminPostService adminPostService; // 오타 수정 및 서비스 주입

    // 관리자 메인 대시보드 (통계 데이터 연동)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", adminUserService.getTotalUserCount());
        model.addAttribute("todaySignups", adminUserService.getTodaySignupCount());
        model.addAttribute("bannedUsers", adminUserService.getBannedUserCount());
        
        // [수정] 실제 게시글 서비스에서 데이터를 가져옵니다.
        model.addAttribute("todayPosts", adminPostService.getTodayPostCount());
        model.addAttribute("pendingReports", 0); // 신고 기능은 추후 확장
        
        return "admin/dashboard"; 
    }

    // 유저 관리 페이지
    @GetMapping("/users")
    public String userManagement(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        
        Page<AdminUserResponseDto> userPage = adminUserService.getUserList(keyword, pageable);
        
        model.addAttribute("users", userPage);
        model.addAttribute("keyword", keyword);
        
        return "admin/user_list"; 
    }
}
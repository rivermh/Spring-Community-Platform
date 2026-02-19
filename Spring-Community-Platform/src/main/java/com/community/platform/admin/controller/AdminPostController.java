package com.community.platform.admin.controller;

import com.community.platform.admin.dto.AdminPostResponseDto;
import com.community.platform.admin.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/posts") // 경로를 /admin/posts로 구분합니다.
@RequiredArgsConstructor
public class AdminPostController {

    private final AdminPostService adminPostService;

    /**
     * 관리자용 게시글 목록 페이지
     */
    @GetMapping
    public String postManagement(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 15, sort = "id") Pageable pageable, // 한 페이지에 15개씩
            Model model) {

        Page<AdminPostResponseDto> postPage = adminPostService.getPostList(keyword, pageable);

        model.addAttribute("posts", postPage);
        model.addAttribute("keyword", keyword); // 검색어 유지

        return "admin/post_list"; 
    }

    /**
     * 게시글 강제 삭제 API (Ajax용)
     */
    @DeleteMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminPostService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
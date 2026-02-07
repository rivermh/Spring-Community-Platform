package com.community.platform.user.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.community.platform.comment.service.CommentService;
import com.community.platform.post.service.PostLikeService;
import com.community.platform.post.service.PostService;
import com.community.platform.security.CustomUserDetails;
import com.community.platform.user.entity.User;
import com.community.platform.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyPageController {

	private final UserService userService;
	private final PostService postService;
	private final CommentService commentService;
	private final PostLikeService postLikeService;

	// 마이페이지 조회
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
	
	@GetMapping("/mypage/posts")
	public String myPosts(@AuthenticationPrincipal CustomUserDetails userDetails,
			@PageableDefault(size = 10) Pageable pageable,Model model) {
		User user = userService.findByUsername(userDetails.getUsername());
		model.addAttribute("posts", postService.findMyPosts(user.getId(), pageable));
		
		return "mypage/posts";
	}
	
	@GetMapping("/mypage/comments")
	public String myComments(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PageableDefault(size = 10) Pageable pageable, Model model) {
		User user = userService.findByUsername(userDetails.getUsername());
		model.addAttribute("comments", commentService.findMyComments(user.getId(), pageable));
		
		return "mypage/comments";
	}
	
	@GetMapping("/mypage/likes")
	public String myLikes(
	        @AuthenticationPrincipal CustomUserDetails userDetails,
	        @PageableDefault(size = 10) Pageable pageable,
	        Model model) {

	    User user = userService.findByUsername(userDetails.getUsername());
	    model.addAttribute("posts",
	            postLikeService.findLikePosts(user.getId(), pageable));

	    return "mypage/likes";
	}

	//탈퇴 처리 
	@PostMapping("/mypage/withdraw")
	public String withdraw(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
		if(userDetails == null) {
			return "redirect:/login";
		}
		
		userService.withdraw(userDetails.getUsername());
		
		//세션/인증 정보 제거 , 강제 로그아웃
		SecurityContextHolder.clearContext();
		request.getSession().invalidate();
		
		return "redirect:/login?withdraw";
	}
}

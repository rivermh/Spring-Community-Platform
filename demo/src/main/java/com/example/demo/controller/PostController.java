package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.PostEditDto;
import com.example.demo.entity.Post;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

	private final PostService postService;
	
	// 게시글 목록
	@GetMapping
	public String list(Model model) {
		List<Post> posts = postService.findAll();
		model.addAttribute("posts", posts);
		return "posts/list";
	}
	
	//글 작성 폼
	@GetMapping("/new")
	public String writerForm() {
		return "posts/form";
	}
	
	//글 작성 처리
	@PostMapping
	public String write(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String title, @RequestParam String content) {
		postService.write(userDetails.getUsername(), title, content);
		
		return "redirect:/posts";
		
	}
	
	//게시글 상세
	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		Post post = postService.findById(id);
		model.addAttribute("post", post);
		
		// 로그인 사용자 정보 전달(중요)
		if(userDetails != null) {
			model.addAttribute("loginUser", userDetails);
		}
		
		return "posts/detail";
	}
	
	//게시글 수정 페이지 (DTO)
	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		// 수정 페이지에 필요한 데이터 조회(DTO)
		PostEditDto post = postService.getEditPost(id, userDetails.getUsername());
		model.addAttribute("post", post);
		
		return "posts/edit";
	}
	
	// 게시글 수정 처리 (POST) 
	@PostMapping("/{id}/edit")
	public String edit(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String title, @RequestParam String content) {
		
		// 실제 게시글 수정(Dirty Checking)
		postService.update(id, userDetails.getUsername(), title, content);
		
		return "redirect:/posts/" + id;
	}
	//게시글 삭제 처리
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
		postService.delete(id, userDetails.getUsername());
		
		return "redirect:/posts";
	}
	
}

package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CommentCreateDto;
import com.example.demo.dto.CommentResponseDto;
import com.example.demo.dto.CommentUpdateDto;
import com.example.demo.entity.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentRestController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> create(
            @PathVariable Long postId,
            @RequestBody CommentCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.create(postId, userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    } 

    // 댓글 목록 조회 ✅
    @GetMapping("/posts/{postId}/comments")
    public Page<CommentResponseDto> comments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User loginUser = (userDetails != null) ? userDetails.getUser() : null;
        return commentService.findByPost(postId, page, loginUser);
    }
    
    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.update(commentId, userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }
   

    // 댓글 삭제 ✅
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.delete(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}


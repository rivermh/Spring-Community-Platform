package com.example.demo.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;

    // 작성자 정보
    private String username;

    // 날짜 (문자열로 내려줌)
    private String createdAt;

    // 자기 댓글 여부
    private boolean mine;

}

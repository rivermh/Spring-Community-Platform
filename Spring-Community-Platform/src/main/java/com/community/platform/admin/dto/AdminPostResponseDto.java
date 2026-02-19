package com.community.platform.admin.dto;

import com.community.platform.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminPostResponseDto {
    private Long id;
    private String title;
    private String author; // 작성자 이름(username)
    private LocalDateTime createdAt;
    private int commentCount; // 댓글 수
    private int likeCount;    // 좋아요 수

    public static AdminPostResponseDto from(Post post) {
        return AdminPostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getUser().getUsername())
                .createdAt(post.getCreatedAt())
                .commentCount(post.getComments().size())
                .likeCount(post.getLikes().size())
                .build();
    }
}
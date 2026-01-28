package com.example.demo.post.dto;

import java.time.LocalDateTime;

public record PostDetailDto(
        Long id,
        String title,
        String content,
        String username,
        int likeCount,
        LocalDateTime createdAt
) {}

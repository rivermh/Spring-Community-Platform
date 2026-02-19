package com.community.platform.admin.service;

import com.community.platform.admin.dto.AdminPostResponseDto;
import com.community.platform.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPostService {

    private final PostRepository postRepository;

    /**
     * 관리자용 게시글 목록 조회 (DTO 변환 포함)
     */
    public Page<AdminPostResponseDto> getPostList(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                    .map(AdminPostResponseDto::from);
        }
        return postRepository.findAll(pageable).map(AdminPostResponseDto::from);
    }

    /**
     * 관리자 권한으로 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    /**
     * 대시보드 통계용: 오늘 게시글 수
     */
    public long getTodayPostCount() {
        LocalDateTime start = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        return postRepository.countByCreatedAtBetween(start, end);
    }
}
package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.entity.User;
import com.example.demo.repository.PostLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
 
    @Transactional
    public boolean toggleLike(Post post, User user) {
        return postLikeRepository.findByPostAndUser(post, user)
                .map(like -> {
                    postLikeRepository.delete(like); // 좋아요 취소
                    return false;
                })
                .orElseGet(() -> {
                    PostLike newLike = PostLike.builder()
                            .post(post)
                            .user(user)
                            .build();
                    postLikeRepository.save(newLike); // 좋아요 등록
                    return true;
                });
    }

    public long getLikeCount(Post post) {
        return postLikeRepository.countByPost(post);
    }
    
    public Page<Post> findLikePosts(Long userId, Pageable pageable) {
        return postLikeRepository.findByUserId(userId, pageable)
                .map(PostLike::getPost);
    }
}

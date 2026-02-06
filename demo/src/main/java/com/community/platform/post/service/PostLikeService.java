package com.community.platform.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.community.platform.post.entity.Post;
import com.community.platform.post.entity.PostLike;
import com.community.platform.post.repository.PostLikeRepository;
import com.community.platform.user.entity.User;

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

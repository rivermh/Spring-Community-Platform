package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.entity.User;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;
	
	@Transactional
	public boolean toggleLike(Long postId, User user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		
		return postLikeRepository.findByPostAndUser(post, user)
				.map(like ->{
					postLikeRepository.delete(like); // 이미 좋아요 했으면 취소
					return false; // 좋아요 취소됨
				})
				.orElseGet(() -> {
					PostLike newLike = PostLike.builder()
							.post(post)
							.user(user)
							.build();
					postLikeRepository.save(newLike); // 좋아요 등록
					return true; // 좋아요 성공
				});
	}
	
	public long getLikeCount(Post post) {
		return postLikeRepository.countByPost(post);
	}
}

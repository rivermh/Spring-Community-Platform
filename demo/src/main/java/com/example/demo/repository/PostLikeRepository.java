package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.entity.User;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	// 특정 유저가 특정 게시글 좋아요 했는지 확인
	Optional<PostLike> findByPostAndUser(Post post, User user);
	
	// 특정 게시글의 좋아요 갯수
	long countByPost(Post post);

}

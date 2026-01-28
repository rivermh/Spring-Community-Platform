package com.example.demo.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.post.entity.Post;
import com.example.demo.post.entity.PostLike;
import com.example.demo.user.entity.User;


public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	// 특정 유저가 특정 게시글 좋아요 했는지 확인
	Optional<PostLike> findByPostAndUser(Post post, User user);
	
	// 특정 게시글의 좋아요 갯수
	long countByPost(Post post);

	//마이페이지 내가 좋아요 한 것
	@EntityGraph(attributePaths = "post.user")
	@Query("""
	    select pl
	    from PostLike pl
	    where pl.user.id = :userId
	    order by pl.id desc
	""")
	Page<PostLike> findByUserId(
	    @Param("userId") Long userId,
	    Pageable pageable
	);
}

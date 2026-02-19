package com.community.platform.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.community.platform.post.entity.Post;


public interface PostRepository extends JpaRepository<Post, Long> {

	@EntityGraph(attributePaths = "user")
	List<Post> findAllByOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = "user")
	Optional<Post> findWithUserById(Long id);

	@EntityGraph(attributePaths = "user")
	@Query("select p from Post p")
	Page<Post> findAllWithUser(Pageable pageable);

	// 제목 검색
	@EntityGraph(attributePaths = "user")
	@Query("""
			    select p from Post p
			    where p.title like %:keyword%
			""")
	Page<Post> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

	// 내용 검색
	@EntityGraph(attributePaths = "user")
	@Query("""
			    select p from Post p
			    where p.content like %:keyword%
			""")
	Page<Post> searchByContent(@Param("keyword") String keyword, Pageable pageable);

	// 작성자(username) 검색
	@EntityGraph(attributePaths = "user")
	@Query("""
			    select p from Post p
			    where p.user.username like %:keyword%
			""")
	Page<Post> searchByAuthor(@Param("keyword") String keyword, Pageable pageable);

	// 마이페이지 내가 쓴 게시글
	@EntityGraph(attributePaths = "user")
	Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	// 1. 관리자용: 제목 또는 내용으로 게시글 검색 (페이징)
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 2. 대시보드용: 특정 기간(오늘 하루) 동안 작성된 게시글 수 카운트
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 3. 통계용: 특정 유저가 작성한 게시글 수 (나중에 유저 상세 페이지에서 유용함)
    long countByUserId(Long userId);
	
}

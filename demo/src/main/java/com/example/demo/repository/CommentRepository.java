package com.example.demo.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	// 특정 게시글의 댓글 목록 조회
	// user를 함께 조회 (작성자 표시용)
	// createdAt 기준 오름차순 (일반적인 댓글 UX)
	
	/*EntityGraph 사용 이유
	 * 댓글 목록에서 comment.getUser().getUsername() 접근
	 * LAZY 로딩
	 * open-in-view=false
	 * LazyInitializationException
	 * Service 안에서 필요한 연관관계를 미리 로딩해야 하기 때문
	 * */
	 
	@EntityGraph(attributePaths = "user")
	Page<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);
	
	//댓글 단건 조회(권한 체크용)
	@EntityGraph(attributePaths = {"user", "post"})
	Comment findWithUserAndPostById(Long id);
	
	// 마이페이지 내가 쓴 댓글
	@EntityGraph(attributePaths = {"user", "post"})
	Page<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
	
}

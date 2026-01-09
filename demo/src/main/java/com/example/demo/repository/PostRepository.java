package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Post;

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

}

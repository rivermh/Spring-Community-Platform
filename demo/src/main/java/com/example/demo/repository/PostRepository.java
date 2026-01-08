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

	
	@Query("""
		    select p from Post p
		    join fetch p.user
		""")
		Page<Post> findAllWithUser(Pageable pageable);

	
	// 제목 검색 (User join 포함) , join fetch p.user > N + 1 방지 
	@Query("select p from Post p join fetch p.user " + " where p.title like %:keyword%")
	Page<Post> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}

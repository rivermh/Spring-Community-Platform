package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.PostSearchType;
import com.example.demo.dto.PostDetailDto;
import com.example.demo.dto.PostEditDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;

	// 글 목록 조회 > List<Post>
	@Transactional(readOnly = true)
	public List<Post> findAll() {
		return postRepository.findAllByOrderByCreatedAtDesc();
	}

	// 글 작성
	public Long write(String username, String title, String content) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		Post post = Post.create(user, title, content);
		postRepository.save(post);

		return post.getId();
	}

	// 글 상세조회
	@Transactional(readOnly = true)
	public PostDetailDto getPostDetail(Long postId) {
		Post post = findById(postId); // 세션 안에서 접근
		return new PostDetailDto(post.getId(), post.getTitle(), post.getContent(), post.getUser().getUsername(),
				post.getLikes().size(), post.getCreatedAt());
	}

	// Entity 직접 조회 > 내부/CRUD용
	public Post findById(Long postId) {
		return postRepository.findWithUserById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
	}

	// 글 수정(수정 화면에 보여줄 데이터 조회)
	// Entity를 직접 노출하지 않고 DTO로 변환하여 반환한다.
	// 작성자 본인만 접근 가능하다
	@Transactional(readOnly = true)
	public PostEditDto getEditPost(Long postId, String username) {
		Post post = findById(postId);

		if (!post.getUser().getUsername().equals(username)) {
			throw new SecurityException("수정 권한이 없습니다.");
		}

		return new PostEditDto(post.getId(), post.getTitle(), post.getContent());
	}

	// 글 수정(DB에 실제로 글 수정 반영)
	// 수정 폼에서 전달받은 값을 반영하여 게시글을 수정한다.
	// 기존 엔티티를 조회한 후 Dirty Checking 방식으로 수정한다.
	// 작성자 본인만 수정 가능하다.
	public void update(Long PostId, String username, String title, String content) {
		Post post = findById(PostId);

		if (!post.getUser().getUsername().equals(username)) {
			throw new SecurityException("수정 권한이 없습니다.");
		}
		post.update(title, content);
	}

	// 글 삭제
	public void delete(Long postId, String username) {
		Post post = findById(postId);

		if (!post.getUser().getUsername().equals(username)) {
			throw new SecurityException("삭제 권한이 없습니다.");
		}
		postRepository.delete(post);
	}

	// 페이징 처리 or 검색기능
	public Page<Post> getPostPage(int page, PostSearchType searchType, String keyword) {
	    Pageable pageable =
	        PageRequest.of(page, 5, Sort.by("id").descending());
	    
	    // 검색어 없으면 전체 조회
	    if(keyword == null || keyword.isBlank()) {
	    	return postRepository.findAllWithUser(pageable);
	    }

	    // 검색 타입 없으면 기본값 (TITLE)
	    if(searchType == null) {
	    	searchType = PostSearchType.TITLE;
	    }
	    
	    
	   return switch(searchType) {
	   case TITLE ->
	   		postRepository.searchByTitle(keyword, pageable);
	   case CONTENT ->
	   		postRepository.searchByTitle(keyword, pageable);
	   case AUTHOR ->
	   		postRepository.searchByTitle(keyword, pageable);
	   };
	}
}

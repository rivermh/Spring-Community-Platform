package com.example.demo.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.comment.dto.CommentCreateDto;
import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.dto.CommentUpdateDto;
import com.example.demo.comment.entity.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.post.entity.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	// 댓글 작성
	public Long create(Long postId, String username, CommentCreateDto dto) {
		// 사용자 조회
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 게시글 조회
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지않습니다."));

		// 댓글 작성
		Comment comment = Comment.create(user, post, dto.getContent());

		// 댓글 저장
		commentRepository.save(comment);

		return comment.getId();

	}

	// 댓글 목록 조회
	@Transactional(readOnly = true)
	public Page<CommentResponseDto> findByPost(
	        Long postId,
	        int page,
	        User loginUser
	) {
	    Pageable pageable = PageRequest.of(page, 5);

	    Page<Comment> commentPage =
	            commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);

	    return commentPage.map(comment -> {

	        boolean isMine = false;
	        if (loginUser != null) {
	            isMine = comment.getUser().getUsername()
	                    .equals(loginUser.getUsername());
	        }

	        return new CommentResponseDto(
	                comment.getId(),
	                comment.getContent(),
	                comment.getUser().getUsername(),
	                comment.getCreatedAt().toString(),
	                isMine
	        );
	    });
	}

	//댓글 수정
	@Transactional
	public void update(Long commentId, String username, CommentUpdateDto dto) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(()-> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		
		// 작성자 검증
		if(!comment.getUser().getUsername().equals(username)) {
			throw new SecurityException("수정 권한이 없습니다.");
		}
		
		comment.update(dto.getContent());
	} // dirty checking으로 update
	
	//댓글 삭제
	public void delete(Long commentId, String username) {
		
		Comment comment = commentRepository.findWithUserAndPostById(commentId);
		
		if(comment == null) {
			throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
		}
		
		// 작성자 검증
		if(!comment.getUser().getUsername().equals(username)) {
			throw new SecurityException("삭제 권한이 없습니다.");
		}
		
		commentRepository.delete(comment);
	}
	
	// 마이페이지 내가 쓴 댓글 보기
	public Page<Comment> findMyComments(Long userId, Pageable pageable){
		return commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
	}
	
}

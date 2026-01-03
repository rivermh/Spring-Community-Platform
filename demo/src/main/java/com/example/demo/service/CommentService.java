package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CommentCreateDto;
import com.example.demo.dto.CommentResponseDto;
import com.example.demo.dto.CommentUpdateDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.CommentUpdateDto;

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
	public List<CommentResponseDto> findByPost(Long postId, User loginUser) {

	    List<Comment> comments =
	            commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

	    return comments.stream()
	            .map(comment -> {

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
	            })
	            .toList();
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
	
}

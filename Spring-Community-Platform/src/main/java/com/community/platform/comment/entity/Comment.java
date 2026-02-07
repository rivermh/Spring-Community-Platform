package com.community.platform.comment.entity;

import java.time.LocalDateTime;

import com.community.platform.post.entity.Post;
import com.community.platform.user.entity.User;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 댓글 작성자 (연관관계 주인은 Comment)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 댓글이 달린 게시글
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	// 댓글 내용
	@Column(nullable = false, length = 500)
	private String content;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	// 생성 시각 자동 생성
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	// 생성 메서드(팩토리)
	public static Comment create(User user, Post post, String content) {
		Comment comment = new Comment();
		comment.user = user;
		comment.post = post;
		comment.content = content;
		return comment;
	}
	
	// 댓글 수정
	public void update(String content) {
		this.content = content;
	}
}

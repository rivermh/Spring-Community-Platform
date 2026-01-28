package com.example.demo.post.entity;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.comment.entity.Comment;
import com.example.demo.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//작성자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	//제목
	@Column(nullable = false, length = 100)
	private String title;
	
	//글
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	// Entity가 DB에 처음 저장되기 직전(Insert 쿼리 날리기 직전)
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	// Entity가 수정되어 update 쿼리 날리기 직전 (DB반영 직전에 시간만 자동 세팅하는 역할)
	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	//생성 메서드
	public static Post create(User user, String title, String content) {
		Post post = new Post();
		post.user = user;
		post.title = title;
		post.content = content;
		return post;
	}
	
	// 수정
	public void update(String title, String content) {
		this.title = title;;
		this.content = content;
	}
	
	// 좋아요 연관 관계 추가
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostLike> likes = new ArrayList<>();
	
	// 게시글 삭제 시 댓글도 같이 삭제 , 부모와 관계 끊긴 자식 자동 제거
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();
}

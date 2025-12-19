package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 50)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	//권한
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;
	
	//가입일
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Builder
	private User(String username, String password, String email, Role role) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
	}
	
	//최초 저장 시 자동으로 생성 시간 세팅
	@PrePersist
	private void prePersist() {
		this.createdAt = LocalDateTime.now();
	}
	
	// 비즈니스 메서드
	public void changePassword(String password) {
		this.password = password;
	}
	
	public void changeEmail(String email) {
		this.email = email;
	}
}


package com.example.demo.entity;

import java.time.LocalDate;
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
	
	// username (Id)
	@Column(nullable = false, unique = true, length = 50)
	private String username;
	
	// 비밀번호
	@Column(nullable = false)
	private String password;
	
	// 이메일
	@Column(nullable = false, unique = true)
	private String email;
	
	// 생년월일
	@Column(nullable = false)
	private LocalDate birth;
	
	// 이메일 인증 전엔 enabled = false
	@Column(nullable = false)
	private boolean enabled;
	
	//권한
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;
	 
	//가입일
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Builder
	private User(String username, String password, String email, LocalDate birth, Role role, boolean enabled) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.birth = birth;
		this.role = role;
		this.enabled = enabled;
	}
	
	//최초 저장 시 자동으로 생성 시간 세팅
	@PrePersist
	private void prePersist() {
		this.createdAt = LocalDateTime.now();
	}
	
	// changePassword
	public void changePassword(String password) {
		this.password = password;
	}
	
	// changeEmail
	public void changeEmail(String email) {
		this.email = email;
	}
}


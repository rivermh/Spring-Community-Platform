package com.community.platform.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.community.platform.common.UserStatus;

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
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;
	 
	//가입일
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Builder
	private User(String username, String password, String email, LocalDate birth, Role role, boolean enabled, UserStatus status) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.birth = birth;
		this.role = role;
		this.enabled = enabled;
		this.status = status;
	}
	
	//최초 저장 시 자동으로 생성 시간 세팅
	@PrePersist
	private void prePersist() {
		this.createdAt = LocalDateTime.now();
	}
	
	// changePassword (비밀번호 변경)
	public void changePassword(String password) {
		this.password = password;
	}
	
	// changeEmail
	public void changeEmail(String email) {
		this.email = email;
	}
	
	// 이메일 인증 완료
	public void activate() {
	    this.status = UserStatus.ACTIVE;
	    this.enabled = true;
	}

	// 이메일 미인증
	public void markInactive() {
	    this.status = UserStatus.INACTIVE;
	    this.enabled = false;
	}

	// 회원 탈퇴
	public void withdraw() {
	    this.status = UserStatus.WITHDRAWN;
	    this.enabled = false;
	}
	
	// 회원 복구
	public void restore() {
	    this.status = UserStatus.ACTIVE;
	    this.enabled = true;
	}
}


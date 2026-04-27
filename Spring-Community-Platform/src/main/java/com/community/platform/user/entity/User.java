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

/**
 * ユーザー情報を管理するEntityクラス (사용자 정보를 관리하는 엔티티 클래스)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// username (Id) // ユーザー名 (ログインIDとして使用)
	@Column(nullable = false, unique = true, length = 50)
	private String username;

	// 비밀번호 // 暗号化されたパスワード
	@Column(nullable = false)
	private String password;

	// 이메일 // メールアドレス (重複不可)
	@Column(nullable = false, unique = true)
	private String email;

	// 생년월일 // 生年月日
	@Column(nullable = false)
	private LocalDate birth;

	// 이메일 인증 전엔 enabled = false // 認証状態 (有効: true, 無効: false)
	@Column(nullable = false)
	private boolean enabled;

	// 권한 // ユーザー権限 (ADMIN, USERなど)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	// ユーザーの状態 (ACTIVE, BANNED, WITHDRAWNなど)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	// 가입일 // 登録日時
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	private User(String username, String password, String email, LocalDate birth, Role role, boolean enabled,
			UserStatus status) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.birth = birth;
		this.role = role;
		this.enabled = enabled;
		this.status = status;
	}

	// 최초 저장 시 자동으로 생성 시간 세팅
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

	/**
	 * メール認証完了時の処理 (이메일 인증 완료 시의 처리)
	 */
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

	/**
	 * 退会処理 (회원 탈퇴 처리)
	 */
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

	
	/**
     * 管理者によるステータス変更 (利用停止など)
     * (관리자에 의한 상태 변경 - 이용 정지 등)
     */
	// 관리자에 의한 상태 변경(정지, 복구 등)
	public void updateStatusByAdmin(UserStatus newStatus) {
		this.status = newStatus;
		// ACTIVE 일 때만 로그인이 가능하도록 enabled 상태를 동기화
		this.enabled = (newStatus == UserStatus.ACTIVE);
	}

	// 관리자에 의한 권한 변경
	public void updateRoleByAdmin(Role newRole) {
		this.role = newRole;
	}

	// BANNED 상태
	public void ban() {
		this.status = UserStatus.BANNED;
		this.enabled = false;
	}
}

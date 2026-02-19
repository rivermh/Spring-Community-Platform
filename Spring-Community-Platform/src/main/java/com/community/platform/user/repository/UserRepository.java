package com.community.platform.user.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.community.platform.common.UserStatus;
import com.community.platform.user.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	
	//이메일 , 비밀번호 중복
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
	// --- 관리자 전용 기능 추가 ---

    /**
     * 1. 관리자용 유저 전체 조회 (페이징)
     * Spring Data JPA가 Pageable을 인자로 받으면 자동으로 페이징 쿼리를 날림
     */
    Page<User> findAll(Pageable pageable);

    /**
     * 2. 아이디 또는 이메일에 특정 키워드가 포함된 유저 검색 (페이징)
     * containing을 사용하면 SQL의 LIKE %keyword%와 같은 효과
     */
    Page<User> findByUsernameContainingOrEmailContaining(String username, String email, Pageable pageable);

    /**
     * 3. 특정 상태(Status)를 가진 유저만 필터링 (페이징)
     * 예: 'BANNED' 상태인 유저만 모아보기
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    //통계용: 특정 기간 내 가입한 유저 수 (오늘 신규 가입자용)
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    //통계용: 특정 상태의 유저 수 (정지 유저 수용)
    long countByStatus(UserStatus status);
}
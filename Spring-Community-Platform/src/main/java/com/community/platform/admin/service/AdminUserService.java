package com.community.platform.admin.service;

import com.community.platform.common.UserStatus;
import com.community.platform.user.entity.Role;
import com.community.platform.user.entity.User;
import com.community.platform.user.repository.UserRepository;
import com.community.platform.admin.dto.AdminUserResponseDto; 
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용으로 설정해 성능 최적화
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * 유저 목록 조회 (검색 및 페이징)
     */
    public Page<AdminUserResponseDto> getUserList(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository.findByUsernameContainingOrEmailContaining(keyword, keyword, pageable)
                    .map(AdminUserResponseDto::from);
        }
        return userRepository.findAll(pageable).map(AdminUserResponseDto::from);
    }

    /**
     * 유저 상태 변경 (정지, 활성화 등)
     */
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status) {
        User user = findUserById(userId);
        user.updateStatusByAdmin(status);
    }

    /**
     * 유저 권한 변경 (승격/강등)
     */
    @Transactional
    public void updateUserRole(Long userId, Role role) {
        User user = findUserById(userId);
        user.updateRoleByAdmin(role);
    }

    /**
     * 유저 영구 정지 (ban 메서드 활용)
     */
    @Transactional
    public void banUser(Long userId) {
        User user = findUserById(userId);
        user.ban();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다. ID: " + userId));
    }
    
    public long getTotalUserCount() {
        return userRepository.count();
    }

    /**
     * 오늘 가입한 신규 유저 수 조회
     */
    public long getTodaySignupCount() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN); // 오늘 00:00:00
        LocalDateTime endOfToday = LocalDateTime.now().with(LocalTime.MAX);   // 오늘 23:59:59
        
        // UserRepository에 findByCreatedAtBetween 메서드가 필요합니다 (아래 설명 참고)
        return userRepository.countByCreatedAtBetween(startOfToday, endOfToday);
    }

    /**
     * [추가] 현재 정지(BANNED)된 유저 수 조회
     */
    public long getBannedUserCount() {
        return userRepository.countByStatus(UserStatus.BANNED);
    }
}
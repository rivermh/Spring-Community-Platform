package com.community.platform.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.platform.admin.dto.AdminUserResponseDto;
import com.community.platform.admin.service.AdminUserService;
import com.community.platform.common.UserStatus;
import com.community.platform.user.entity.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/api/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final AdminUserService adminService;
	
	// 유저 전체 목록 조회 및 검색
	@GetMapping
	public ResponseEntity<Page<AdminUserResponseDto>> getUserList(
			@RequestParam(required = false) String keyword,
			@PageableDefault(size = 10) Pageable pageable){
		Page<AdminUserResponseDto> users = adminService.getUserList(keyword, pageable);
		return ResponseEntity.ok(users);
	}
	
	// 유저 상태 변경 ( 정지, 확성화 등)
	@PatchMapping("/{userId}/status")
	public ResponseEntity<Void> updateUserStatus(
			@PathVariable Long userId,
			@RequestParam UserStatus status){
		adminService.updateUserStatus(userId, status);
		return ResponseEntity.ok().build();
	}
	
	// 유저 권한 변경(관리자 승격 등)
	@PatchMapping("/{userId}/role")
	public ResponseEntity<Void> updateUserRole(
			@PathVariable Long userId,
			@RequestParam Role role){
		adminService.updateUserRole(userId, role);
		return ResponseEntity.ok().build();
	
	}
	
	// 유저 영구 정지 (BAN)
    @PostMapping("/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }
}

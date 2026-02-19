package com.community.platform.admin.dto;

import com.community.platform.user.entity.User;
import com.community.platform.user.entity.Role;
import com.community.platform.common.UserStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class AdminUserResponseDto {
    private Long id;
    private String username;
    private String email;
    private UserStatus status;
    private Role role;
    private LocalDateTime createdAt;
    private boolean enabled;

    public static AdminUserResponseDto from(User user) {
        return AdminUserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .enabled(user.isEnabled())
                .build();
    }
}
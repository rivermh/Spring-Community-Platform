package com.community.platform.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.community.platform.user.entity.User;
import com.community.platform.common.UserStatus;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /* ===== 계정 상태 체크 로직 (수정됨) ===== */

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * 관리자가 정지하거나 사용자가 탈퇴해서 enabled가 false면 로그인 차단
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * 계정 잠금 여부
     * 상태가 BANNED인 경우 잠긴 계정으로 간주하여 차단
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.BANNED;
    }

    public User getUser() {
        return user;
    }
}
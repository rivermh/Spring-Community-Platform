package com.community.platform.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.community.platform.user.entity.User;

;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }
  
    /**
     * 권한 정보
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    /**
     * 비밀번호 (BCrypt 암호화된 값)
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 로그인 ID (username)
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /* ===== 계정 상태 (일단 전부 true) ===== */

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // 일단 로그인 프로세스는 타게 함
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 여기서 false를 주면 LockedException이 발생함
    }

    /* ===== 필요하면 User 꺼내 쓰기 ===== */
    public User getUser() {
        return user;
    }
}

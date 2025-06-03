package com.speako.domain.security.adapter;

import com.speako.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    public static CustomUserDetails toCustomUserDetails(User user) {
        return new CustomUserDetails(user);
    }

    @Override
    // 권한을 반환 (권한 관련 기능은 아직 없음)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    // 이메일을 유저이름으로서 반환
    public String getUsername() {
        return getEmail();
    }

    @Override
    // 계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    // 계정 잠김 여부 반환
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    // 자격 증명 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    // 계정 활성화 여부 반환
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

package com.xiazihan.webback.security;

import com.xiazihan.webback.model.entity.SysUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Data
@Slf4j
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final SysUser user;

    public UserDetailsImpl(SysUser user) {
        if (user == null) {
            log.error("创建UserDetailsImpl时用户对象为空");
            throw new IllegalArgumentException("用户对象不能为空");
        }
        this.user = user;
        log.info("创建UserDetailsImpl成功，用户名: {}", user.getUsername());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = Optional.ofNullable(user.getRole()).orElse("ROLE_READER");
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        String password = user.getPassword();
        log.debug("获取用户密码，用户名: {}", user.getUsername());
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
}
package com.xiazihan.webback.service;

import com.xiazihan.webback.model.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    /**
     * 通过用户名查找用户
     */
    SysUser getByUsername(String username);

    /**
     * 注册用户
     */
    void register(SysUser user);

    /**
     * 更新用户登录时间
     */
    void updateLoginTime(String username);
}
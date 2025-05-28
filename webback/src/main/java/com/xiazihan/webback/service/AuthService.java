package com.xiazihan.webback.service;

import com.xiazihan.webback.model.dto.LoginDTO;
import com.xiazihan.webback.model.dto.RegisterDTO;
import com.xiazihan.webback.model.vo.LoginVO;

public interface AuthService {
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 退出登录
     */
    void logout(String username);
}
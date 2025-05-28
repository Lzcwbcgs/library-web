package com.xiazihan.webback.service.impl;

import com.xiazihan.webback.model.dto.LoginDTO;
import com.xiazihan.webback.model.dto.RegisterDTO;
import com.xiazihan.webback.model.entity.SysUser;
import com.xiazihan.webback.model.vo.LoginVO;
import com.xiazihan.webback.model.vo.UserVO;
import com.xiazihan.webback.security.UserDetailsImpl;
import com.xiazihan.webback.service.AuthService;
import com.xiazihan.webback.service.UserService;
import com.xiazihan.webback.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;    @Override
    public LoginVO login(LoginDTO loginDTO) {
        try {
            log.info("开始登录验证，用户名: {}", loginDTO.getUsername());
            
            // 进行身份验证
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            log.info("身份验证成功，用户名: {}", loginDTO.getUsername());

            // 生成token
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            log.info("Token生成成功");

            // 更新登录时间
            userService.updateLoginTime(userDetails.getUsername());
            log.info("登录时间更新成功");

            // 构建返回信息
            SysUser currentUser = userDetails.getUser();
            if (currentUser == null) {
                log.error("用户对象为空");
                throw new IllegalStateException("用户对象为空");
            }
            
            log.info("构建用户信息，ID: {}, 用户名: {}", currentUser.getId(), currentUser.getUsername());
            UserVO userVO = UserVO.builder()
                    .id(currentUser.getId())
                    .username(currentUser.getUsername())
                    .realName(currentUser.getRealName())
                    .phone(currentUser.getPhone())
                    .email(currentUser.getEmail())
                    .avatar(currentUser.getAvatar())
                    .role(currentUser.getRole())
                    .creditScore(currentUser.getCreditScore())
                    .build();

            LoginVO loginVO = LoginVO.builder()
                    .token(token)
                    .userInfo(userVO)
                    .build();
            
            log.info("登录成功，返回登录信息");
            return loginVO;
        } catch (Exception e) {
            log.error("登录过程中发生异常", e);
            throw e;
        }
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(registerDTO, user);
        // 设置默认角色
        user.setRole("ROLE_READER");
        userService.register(user);
    }

    @Override
    public void logout(String username) {
        // 由于使用JWT，这里暂时不需要实现
        // 如果需要黑名单功能，可以在这里实现
    }
}
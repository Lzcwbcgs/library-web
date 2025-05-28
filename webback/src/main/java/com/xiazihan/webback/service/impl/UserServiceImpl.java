package com.xiazihan.webback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.mapper.UserMapper;
import com.xiazihan.webback.model.entity.SysUser;
import com.xiazihan.webback.security.UserDetailsImpl;
import com.xiazihan.webback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("开始加载用户信息: {}", username);
            SysUser user = getByUsername(username);
            
            if (user == null) {
                log.warn("用户不存在: {}", username);
                throw new UsernameNotFoundException("用户名或密码错误");
            }
            
            if (user.getStatus() != 1) {
                log.warn("用户已被禁用: {}", username);
                throw new UsernameNotFoundException("账号已被禁用");
            }
            
            log.info("用户信息加载成功: {}, 密码: {}", username, user.getPassword());
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            log.debug("创建UserDetails成功: {}, 角色: {}", username, userDetails.getAuthorities());
            return userDetails;
        } catch (UsernameNotFoundException e) {
            log.error("认证失败 - {}: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("加载用户信息时发生错误: {}", username, e);
            throw new UsernameNotFoundException("系统错误");
        }
    }

    @Override
    public SysUser getByUsername(String username) {
        try {
            log.debug("查询用户: {}", username);
            return userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, username));
        } catch (Exception e) {
            log.error("查询用户信息出错: {}", username, e);
            throw new RuntimeException("数据库查询失败");
        }
    }

    @Override
    public void register(SysUser user) {
        try {
            log.info("开始注册用户: {}", user.getUsername());
            
            // 检查用户名是否存在
            if (getByUsername(user.getUsername()) != null) {
                log.warn("用户名已存在: {}", user.getUsername());
                throw new BusinessException(ResultCode.USERNAME_ALREADY_EXIST);
            }
            
            // 验证密码
            String rawPassword = user.getPassword();
            if (rawPassword == null || rawPassword.trim().length() < 6) {
                log.error("密码不符合要求");
                throw new BusinessException(ResultCode.PASSWORD_NOT_VALID);
            }
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            log.debug("密码加密完成");
            
            // 设置默认值
            user.setStatus(1);  // 1: 正常
            user.setCreditScore(100);  // 初始信用分
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            
            // 设置角色（如果未设置）
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("ROLE_READER");
            }
            
            // 插入数据库
            int rows = userMapper.insert(user);
            if (rows != 1) {
                log.error("用户数据插入失败: {}", user.getUsername());
                throw new BusinessException(ResultCode.REGISTER_FAILED);
            }
            
            log.info("用户注册成功: {}, ID: {}", user.getUsername(), user.getId());
        } catch (BusinessException e) {
            log.warn("用户注册业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("注册用户失败: {}", user.getUsername(), e);
            throw new BusinessException(ResultCode.REGISTER_FAILED);
        }
    }

    @Override
    public void updateLoginTime(String username) {
        try {
            SysUser user = getByUsername(username);
            if (user != null) {
                user.setLastLoginTime(LocalDateTime.now());
                userMapper.updateById(user);
                log.info("更新用户登录时间成功: {}", username);
            } else {
                log.warn("更新登录时间失败，未找到用户: {}", username);
            }
        } catch (Exception e) {
            log.error("更新用户登录时间失败", e);
        }
    }
}
package com.xiazihan.webback.utils;

import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.model.entity.SysUser;
import com.xiazihan.webback.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// 如果需要日志，可以导入 import lombok.extern.slf4j.Slf4j; 并添加 @Slf4j 注解

public class SecurityUtils {

    private static UserDetailsImpl getCurrentUserDetailsPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 检查认证对象和 principal 是否存在
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            // log.warn("Authentication or principal is null or not authenticated.");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录或认证信息无效");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        } else if (principal instanceof String && "anonymousUser".equals(principal)) {
            // log.info("Request made by anonymousUser");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "匿名用户，请先登录");
        } else {
            // log.error("Principal is not an instance of UserDetailsImpl. Actual type: {}", principal.getClass().getName());
            // System.out.println("Principal is not an instance of UserDetailsImpl. Actual type: " + principal.getClass().getName()); // 临时调试
            throw new BusinessException(ResultCode.FAILED, "无法获取标准用户详情对象");
        }
    }
    
    public static Long getCurrentUserId() {
        UserDetailsImpl userDetails = getCurrentUserDetailsPrincipal();
        SysUser sysUser = userDetails.getUser(); // UserDetailsImpl has getUser() returning SysUser
        
        if (sysUser == null) {
            // log.error("SysUser object within UserDetailsImpl is null for principal: {}", userDetails.getUsername());
            throw new BusinessException(ResultCode.FAILED, "用户账户内部数据错误");
        }
        
        // Assumes SysUser entity has a getId() method returning Long
        if (sysUser.getId() == null) {
            // log.error("SysUser ID is null for principal: {}", userDetails.getUsername());
             throw new BusinessException(ResultCode.FAILED, "用户ID未找到");
        }
        return sysUser.getId(); 
    }

    public static String getCurrentUsername() {
        // UserDetailsImpl directly implements getUsername()
        return getCurrentUserDetailsPrincipal().getUsername(); 
    }
}
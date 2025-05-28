package com.xiazihan.webback.controller;

import com.xiazihan.webback.common.api.ApiResult;
import com.xiazihan.webback.model.vo.UserProfileVO;
import com.xiazihan.webback.service.UserService; // 假设你有一个 UserService
import com.xiazihan.webback.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user") // 如果 context-path=/api, 实际路径是 /api/user
@RequiredArgsConstructor
@Slf4j
public class UserController {

    // private final UserService userService; // 如果 UserProfileVO 需要从数据库等获取，则注入 UserService

    @Operation(summary = "获取用户个人信息")
    @GetMapping("/profile")
    public ApiResult<UserProfileVO> getProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUsername = SecurityUtils.getCurrentUsername();
        
        log.info("获取用户 {} ({}) 的个人信息", currentUserId, currentUsername);
        
        UserProfileVO userProfile = new UserProfileVO();
        userProfile.setUserId(currentUserId);
        userProfile.setUsername(currentUsername);
        
        // 如果你有更详细的用户信息服务，可以替换下面的逻辑
        // userProfile = userService.getUserProfile(currentUserId);
        
        return ApiResult.success(userProfile);
    }

    // 可以在这里添加其他用户相关的端点，例如：
    /*
    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public ApiResult<Void> updateProfile(@RequestBody UpdateProfileDTO_NEEDS_TO_BE_CREATED dto) { // 你需要创建 UpdateProfileDTO
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("用户 {} 正在更新个人信息", userId);
        // userService.updateProfile(userId, dto); // 调用服务层方法
        return ApiResult.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public ApiResult<Void> changePassword(@RequestBody ChangePasswordDTO_NEEDS_TO_BE_CREATED dto) { // 你需要创建 ChangePasswordDTO
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("用户 {} 正在修改密码", userId);
        // userService.changePassword(userId, dto); // 调用服务层方法
        return ApiResult.success();
    }
    */
}

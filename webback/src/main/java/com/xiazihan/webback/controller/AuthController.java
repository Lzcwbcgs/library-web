package com.xiazihan.webback.controller;

import com.xiazihan.webback.common.api.ApiResult;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.model.dto.LoginDTO;
import com.xiazihan.webback.model.dto.RegisterDTO;
import com.xiazihan.webback.model.vo.LoginVO;
import com.xiazihan.webback.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            log.info("接收到登录请求，用户名: {}", loginDTO.getUsername());
            LoginVO loginVO = authService.login(loginDTO);
            log.info("用户登录成功: {}", loginDTO.getUsername());
            return ApiResult.success(loginVO);
        } catch (BusinessException e) {
            log.warn("用户登录业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "登录失败，请稍后重试");
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResult<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            log.info("接收到注册请求，用户名: {}", registerDTO.getUsername());
            authService.register(registerDTO);
            log.info("用户注册成功: {}", registerDTO.getUsername());
            return ApiResult.success();
        } catch (BusinessException e) {
            log.warn("用户注册业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "注册失败，请稍后重试");
        }
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResult<Void> logout(Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : "未知用户";
            log.info("接收到退出登录请求，用户: {}", username);
            authService.logout(username);
            log.info("用户退出登录成功: {}", username);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("退出登录异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "退出登录失败，请稍后重试");
        }
    }
}
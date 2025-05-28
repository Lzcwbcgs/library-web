package com.xiazihan.webback.common.exception;

import com.xiazihan.webback.common.api.ApiResult;
import com.xiazihan.webback.common.api.ResultCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResult<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return ApiResult.failed(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResult<Object> handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证异常：{}", e.getMessage());
        return ApiResult.failed(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResult<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常：{}", e.getMessage());
        return ApiResult.failed(ResultCode.FORBIDDEN);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ApiResult<Object> handleValidationException(Exception e) {
        log.error("参数校验异常：{}", e.getMessage());
        return ApiResult.failed(ResultCode.VALIDATE_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Object> handleException(Exception e) {
        log.error("系统异常：", e);
        return ApiResult.failed(ResultCode.FAILED);
    }
}
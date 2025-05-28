package com.xiazihan.webback.common.api;

import lombok.Getter;

@Getter
public enum ResultCode implements IResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    
    // 用户相关错误码
    USER_NOT_EXIST(1001, "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ACCOUNT_LOCKED(1003, "账号已被锁定"),
    USER_ACCOUNT_INVALID(1004, "账号状态异常"),
    USERNAME_ALREADY_EXIST(1005, "用户名已存在"),
    PASSWORD_ERROR(1006, "密码错误"),
    PASSWORD_NOT_VALID(1007, "密码不符合要求"),
    REGISTER_FAILED(1008, "注册失败"),
    USER_DISABLED(1009, "账号已被禁用"),
    
    // 图书相关错误码
    BOOK_NOT_EXIST(2001, "图书不存在"),
    BOOK_STOCK_NOT_ENOUGH(2002, "图书库存不足"),
    BOOK_ALREADY_BORROWED(2003, "图书已被借出"),
    BOOK_NOT_BORROWED(2004, "图书未被借出"),
    ISBN_ALREADY_EXIST(2005, "ISBN已存在"),
    
    // 借阅相关错误码
    BORROW_RECORD_NOT_EXIST(3001, "借阅记录不存在"),
    BOOK_OVERDUE(3002, "图书已逾期"),
    EXCEED_MAXIMUM_BORROW_COUNT(3003, "超出最大借阅数量"),
    RENEW_NOT_ALLOWED(3004, "不满足续借条件"),
    BOOK_ALREADY_RETURNED(3005, "图书已归还"),
    HAS_UNPAID_FINE(3006, "存在未缴纳的罚款");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
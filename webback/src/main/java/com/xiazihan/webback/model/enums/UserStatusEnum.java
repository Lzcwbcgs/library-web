package com.xiazihan.webback.model.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "正常");

    private final Integer code;
    private final String desc;

    UserStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
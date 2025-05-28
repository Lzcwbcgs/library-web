package com.xiazihan.webback.model.enums;

import lombok.Getter;

@Getter
public enum BookStatusEnum {
    DISABLED(0, "下架"),
    ENABLED(1, "正常");

    private final Integer code;
    private final String desc;

    BookStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 
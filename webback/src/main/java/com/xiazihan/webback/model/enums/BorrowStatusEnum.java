package com.xiazihan.webback.model.enums;

import lombok.Getter;

@Getter
public enum BorrowStatusEnum {
    BORROWING(0, "借阅中"),
    RETURNED(1, "已归还"),
    OVERDUE(2, "逾期未还"),
    OVERDUE_RETURNED(3, "逾期已还");

    private final Integer code;
    private final String desc;

    BorrowStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
package com.xiazihan.webback.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowDTO {
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    @NotNull(message = "借阅天数不能为空")
    private Integer borrowDays;
}
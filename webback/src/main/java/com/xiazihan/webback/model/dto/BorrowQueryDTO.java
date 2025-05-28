package com.xiazihan.webback.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class BorrowQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    private String keyword;  // 用户名/图书名/ISBN
    private Long userId;
    private Long bookId;
    private Integer status;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
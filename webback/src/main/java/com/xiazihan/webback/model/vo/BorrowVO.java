package com.xiazihan.webback.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BorrowVO {
    private Long id;
    
    private Long userId;
    private String username;
    private String userRealName;
      private Long bookId;
    private String bookName;
    private String bookIsbn;
    private String author;
    
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    
    private Integer renewCount;
    private Integer status;
    private String statusDesc;
    
    private BigDecimal fineAmount;
    private Integer finePaid;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
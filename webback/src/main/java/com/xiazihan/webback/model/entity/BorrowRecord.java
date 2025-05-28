package com.xiazihan.webback.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("borrow_record")
public class BorrowRecord extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long bookId;
    
    private LocalDateTime borrowTime;
    
    private LocalDateTime dueTime;
    
    private LocalDateTime returnTime;
    
    private Integer renewCount;
    
    private Integer status;
    
    private BigDecimal fineAmount;
    
    private Integer finePaid;
} 
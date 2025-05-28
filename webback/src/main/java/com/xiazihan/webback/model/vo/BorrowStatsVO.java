package com.xiazihan.webback.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BorrowStatsVO {
    // 总借阅次数
    private Integer totalBorrowCount;
    
    // 当前借阅中数量
    private Integer currentBorrowCount;
    
    // 已归还数量
    private Integer returnedCount;
    
    // 逾期未还数量
    private Integer overdueCount;
    
    // 逾期已还数量
    private Integer overdueReturnedCount;
    
    // 总罚金金额
    private BigDecimal totalFineAmount;
    
    // 已缴纳罚金金额
    private BigDecimal paidFineAmount;
    
    // 未缴纳罚金金额
    private BigDecimal unpaidFineAmount;
}
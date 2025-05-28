package com.xiazihan.webback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiazihan.webback.model.dto.BorrowDTO;
import com.xiazihan.webback.model.dto.BorrowQueryDTO;
import com.xiazihan.webback.model.vo.BorrowStatsVO;
import com.xiazihan.webback.model.vo.BorrowVO;

public interface BorrowService {
    /**
     * 创建借阅记录
     */
    void createBorrow(Long userId, BorrowDTO borrowDTO);

    /**
     * 续借
     */
    void renewBorrow(Long userId, Long borrowId);

    /**
     * 归还图书
     */
    void returnBook(Long userId, Long borrowId);

    /**
     * 获取借阅记录详情
     */
    BorrowVO getBorrowDetail(Long borrowId);

    /**
     * 分页查询借阅记录
     */
    IPage<BorrowVO> getBorrowPage(BorrowQueryDTO queryDTO);

    /**
     * 处理逾期记录
     */
    void handleOverdueRecords();

    /**
     * 获取借阅统计数据
     */
    BorrowStatsVO getBorrowStats();

    /**
     * 获取用户借阅统计数据
     */
    BorrowStatsVO getUserBorrowStats(Long userId);
}
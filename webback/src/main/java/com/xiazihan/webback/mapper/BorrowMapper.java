package com.xiazihan.webback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiazihan.webback.model.dto.BorrowQueryDTO;
import com.xiazihan.webback.model.entity.BorrowRecord;
import com.xiazihan.webback.model.vo.BorrowStatsVO;
import com.xiazihan.webback.model.vo.BorrowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface BorrowMapper extends BaseMapper<BorrowRecord> {
    
    /**
     * 查询用户未归还的指定图书数量
     */
    int selectBorrowingCount(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    /**
     * 获取借阅记录详情
     */
    BorrowVO selectBorrowDetail(@Param("id") Long id);
    
    /**
     * 分页查询借阅记录
     */
    IPage<BorrowVO> selectBorrowPage(Page<BorrowVO> page, @Param("query") BorrowQueryDTO queryDTO);
    
    /**
     * 更新逾期状态
     */
    void updateOverdueStatus(@Param("now") LocalDateTime now);
    
    /**
     * 获取借阅统计数据
     */
    BorrowStatsVO selectBorrowStats(@Param("userId") Long userId);
}
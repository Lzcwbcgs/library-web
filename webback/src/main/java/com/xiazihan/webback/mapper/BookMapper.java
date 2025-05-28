package com.xiazihan.webback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiazihan.webback.model.entity.BookInfo;
import com.xiazihan.webback.model.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper extends BaseMapper<BookInfo> {
    /**
     * 分页查询图书信息
     */
    IPage<BookVO> selectBookPage(Page<BookVO> page, @Param("keyword") String keyword, 
                                @Param("categoryId") Long categoryId, @Param("status") Integer status);
}
package com.xiazihan.webback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiazihan.webback.model.dto.BookDTO;
import com.xiazihan.webback.model.dto.BookQueryDTO;
import com.xiazihan.webback.model.entity.BookInfo;
import com.xiazihan.webback.model.vo.BookVO;

public interface BookService {
    /**
     * 添加图书
     */
    void addBook(BookDTO bookDTO);

    /**
     * 更新图书信息
     */
    void updateBook(Long id, BookDTO bookDTO);

    /**
     * 删除图书
     */
    void deleteBook(Long id);

    /**
     * 获取图书详情
     */
    BookVO getBookDetail(Long id);

    /**
     * 分页查询图书
     */
    IPage<BookVO> getBookPage(BookQueryDTO queryDTO);

    /**
     * 更新图书状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 更新图书库存
     */
    void updateStock(Long id, Integer changeCount);
}
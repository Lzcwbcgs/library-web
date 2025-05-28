package com.xiazihan.webback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.mapper.BookMapper;
import com.xiazihan.webback.model.dto.BookDTO;
import com.xiazihan.webback.model.dto.BookQueryDTO;
import com.xiazihan.webback.model.entity.BookInfo;
import com.xiazihan.webback.model.vo.BookVO;
import com.xiazihan.webback.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBook(BookDTO bookDTO) {
        // 检查ISBN是否已存在
        if (isIsbnExists(bookDTO.getIsbn())) {
            throw new BusinessException(ResultCode.ISBN_ALREADY_EXIST);
        }

        BookInfo bookInfo = new BookInfo();
        BeanUtils.copyProperties(bookDTO, bookInfo);
        bookInfo.setAvailableStock(bookDTO.getTotalStock());
        bookInfo.setStatus(1);
        bookInfo.setBorrowCount(0);

        bookMapper.insert(bookInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Long id, BookDTO bookDTO) {
        BookInfo bookInfo = getBookById(id);
        
        // 如果修改了ISBN，需要检查新的ISBN是否已存在
        if (!bookInfo.getIsbn().equals(bookDTO.getIsbn()) && isIsbnExists(bookDTO.getIsbn())) {
            throw new BusinessException(ResultCode.ISBN_ALREADY_EXIST);
        }

        BeanUtils.copyProperties(bookDTO, bookInfo);
        // 计算可借库存变化
        int stockChange = bookDTO.getTotalStock() - bookInfo.getTotalStock();
        bookInfo.setAvailableStock(bookInfo.getAvailableStock() + stockChange);

        bookMapper.updateById(bookInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        BookInfo bookInfo = getBookById(id);
        if (bookInfo.getAvailableStock() < bookInfo.getTotalStock()) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "该图书尚有借出未还，不能删除");
        }
        bookMapper.deleteById(id);
    }

    @Override
    public BookVO getBookDetail(Long id) {
        BookInfo bookInfo = getBookById(id);
        BookVO bookVO = new BookVO();
        BeanUtils.copyProperties(bookInfo, bookVO);
        return bookVO;
    }

    @Override
    public IPage<BookVO> getBookPage(BookQueryDTO queryDTO) {
        Page<BookVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return bookMapper.selectBookPage(page, queryDTO.getKeyword(), 
                queryDTO.getCategoryId(), queryDTO.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        BookInfo bookInfo = getBookById(id);
        bookInfo.setStatus(status);
        bookMapper.updateById(bookInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(Long id, Integer changeCount) {
        BookInfo bookInfo = getBookById(id);
        
        // 检查库存是否足够
        if (bookInfo.getAvailableStock() + changeCount < 0) {
            throw new BusinessException(ResultCode.BOOK_STOCK_NOT_ENOUGH);
        }

        bookInfo.setAvailableStock(bookInfo.getAvailableStock() + changeCount);
        if (changeCount < 0) {
            bookInfo.setBorrowCount(bookInfo.getBorrowCount() + Math.abs(changeCount));
        }

        bookMapper.updateById(bookInfo);
    }

    private BookInfo getBookById(Long id) {
        BookInfo bookInfo = bookMapper.selectById(id);
        if (bookInfo == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_EXIST);
        }
        return bookInfo;
    }

    private boolean isIsbnExists(String isbn) {
        return bookMapper.selectCount(new LambdaQueryWrapper<BookInfo>()
                .eq(BookInfo::getIsbn, isbn)) > 0;
    }
}
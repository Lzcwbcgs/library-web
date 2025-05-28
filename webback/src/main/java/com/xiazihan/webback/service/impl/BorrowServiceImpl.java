package com.xiazihan.webback.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.mapper.BookMapper;
import com.xiazihan.webback.mapper.BorrowMapper;
import com.xiazihan.webback.mapper.UserMapper;
import com.xiazihan.webback.model.dto.BorrowDTO;
import com.xiazihan.webback.model.dto.BorrowQueryDTO;
import com.xiazihan.webback.model.entity.BookInfo;
import com.xiazihan.webback.model.entity.BorrowRecord;
import com.xiazihan.webback.model.entity.SysUser;
import com.xiazihan.webback.model.enums.BorrowStatusEnum;
import com.xiazihan.webback.model.vo.BorrowStatsVO;
import com.xiazihan.webback.model.vo.BorrowVO;
import com.xiazihan.webback.service.BookService;
import com.xiazihan.webback.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowMapper borrowMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BookService bookService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBorrow(Long userId, BorrowDTO borrowDTO) {
        // 检查用户是否存在且状态正常
        SysUser user = checkUser(userId);
        
        // 检查图书是否存在且可借
        BookInfo book = checkBook(borrowDTO.getBookId());
        
        // 检查用户是否有未还的相同图书
        checkExistingBorrow(userId, borrowDTO.getBookId());
        
        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(borrowDTO.getBookId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(record.getBorrowTime().plusDays(borrowDTO.getBorrowDays()));
        record.setStatus(BorrowStatusEnum.BORROWING.getCode());
        record.setRenewCount(0);
        record.setFineAmount(BigDecimal.ZERO);
        record.setFinePaid(0);
        
        borrowMapper.insert(record);
        
        // 更新图书库存
        bookService.updateStock(borrowDTO.getBookId(), -1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renewBorrow(Long userId, Long borrowId) {
        BorrowRecord record = getBorrowRecord(borrowId);
        
        // 检查是否是本人的借阅记录
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "只能续借自己的借阅记录");
        }
        
        // 检查是否可续借
        if (!BorrowStatusEnum.BORROWING.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "只能续借正在借阅中的记录");
        }
        
        // 检查续借次数
        if (record.getRenewCount() >= 2) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "最多续借2次");
        }
        
        // 更新到期时间和续借次数
        record.setDueTime(record.getDueTime().plusDays(7));
        record.setRenewCount(record.getRenewCount() + 1);
        
        borrowMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, Long borrowId) {
        BorrowRecord record = getBorrowRecord(borrowId);
        
        // 检查是否是本人的借阅记录
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "只能归还自己借阅的图书");
        }
        
        // 检查是否已归还
        if (BorrowStatusEnum.RETURNED.getCode().equals(record.getStatus()) ||
            BorrowStatusEnum.OVERDUE_RETURNED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "该记录已归还");
        }
        
        LocalDateTime now = LocalDateTime.now();
        record.setReturnTime(now);
        
        // 检查是否逾期
        if (now.isAfter(record.getDueTime())) {
            record.setStatus(BorrowStatusEnum.OVERDUE_RETURNED.getCode());
            // 计算罚金（每天1元）
            long overdueDays = ChronoUnit.DAYS.between(record.getDueTime(), now);
            record.setFineAmount(new BigDecimal(overdueDays));
        } else {
            record.setStatus(BorrowStatusEnum.RETURNED.getCode());
        }
        
        borrowMapper.updateById(record);
        
        // 更新图书库存
        bookService.updateStock(record.getBookId(), 1);
    }

    @Override
    public BorrowVO getBorrowDetail(Long borrowId) {
        return borrowMapper.selectBorrowDetail(borrowId);
    }

    @Override
    public IPage<BorrowVO> getBorrowPage(BorrowQueryDTO queryDTO) {
        Page<BorrowVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return borrowMapper.selectBorrowPage(page, queryDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOverdueRecords() {
        // 获取所有借阅中且已过期的记录
        LocalDateTime now = LocalDateTime.now();
        borrowMapper.updateOverdueStatus(now);
    }

    @Override
    public BorrowStatsVO getBorrowStats() {
        return borrowMapper.selectBorrowStats(null);
    }

    @Override
    public BorrowStatsVO getUserBorrowStats(Long userId) {
        return borrowMapper.selectBorrowStats(userId);
    }

    private SysUser checkUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "用户已被禁用");
        }
        return user;
    }

    private BookInfo checkBook(Long bookId) {
        BookInfo book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_EXIST);
        }
        if (book.getStatus() == 0) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "图书已下架");
        }
        if (book.getAvailableStock() <= 0) {
            throw new BusinessException(ResultCode.BOOK_STOCK_NOT_ENOUGH);
        }
        return book;
    }

    private void checkExistingBorrow(Long userId, Long bookId) {
        int count = borrowMapper.selectBorrowingCount(userId, bookId);
        if (count > 0) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "您已借阅此书且未归还");
        }
    }

    private BorrowRecord getBorrowRecord(Long borrowId) {
        BorrowRecord record = borrowMapper.selectById(borrowId);
        if (record == null) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "借阅记录不存在");
        }
        return record;
    }
}
package com.xiazihan.webback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiazihan.webback.common.api.ApiResult;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.model.dto.BorrowDTO;
import com.xiazihan.webback.model.dto.BorrowQueryDTO;
import com.xiazihan.webback.model.vo.BorrowStatsVO;
import com.xiazihan.webback.model.vo.BorrowVO;
import com.xiazihan.webback.service.BorrowService;
import com.xiazihan.webback.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
@Slf4j
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    public ApiResult<Void> createBorrow(@Valid @RequestBody BorrowDTO borrowDTO) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("创建借阅记录请求, userId: {}, bookId: {}", userId, borrowDTO.getBookId());
            borrowService.createBorrow(userId, borrowDTO);
            log.info("借阅成功, userId: {}, bookId: {}", userId, borrowDTO.getBookId());
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("创建借阅业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建借阅系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "借阅失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/return")  // 添加对PUT方法的支持
    public ApiResult<Void> returnBookPut(@PathVariable("id") Long borrowId, HttpServletRequest request) {
        return returnBook(borrowId, request);
    }

    @PostMapping("/{id}/return")
    public ApiResult<Void> returnBook(@PathVariable("id") Long borrowId, HttpServletRequest request) {
        try {
            log.info("还书请求, borrowId: {}, method: {}", borrowId, request.getMethod());
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("还书请求用户信息, userId: {}", userId);
            borrowService.returnBook(userId, borrowId);
            log.info("还书成功, userId: {}, borrowId: {}", userId, borrowId);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("还书业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("还书系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "还书失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/renew")
    public ApiResult<Void> renewBorrow(@PathVariable("id") Long borrowId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("续借请求, userId: {}, borrowId: {}", userId, borrowId);
            borrowService.renewBorrow(userId, borrowId);
            log.info("续借成功, userId: {}, borrowId: {}", userId, borrowId);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("续借业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("续借系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "续借失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResult<IPage<BorrowVO>> getBorrowPage(BorrowQueryDTO queryDTO) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("查询借阅记录请求, userId: {}, queryDTO: {}", userId, queryDTO);
            queryDTO.setUserId(userId);
            IPage<BorrowVO> page = borrowService.getBorrowPage(queryDTO);
            return ApiResult.success(page);
        } catch (BusinessException e) {
            log.error("查询借阅记录业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询借阅记录系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "查询借阅记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/handle-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> handleOverdueRecords() {
        try {
            log.info("处理逾期记录请求");
            borrowService.handleOverdueRecords();
            log.info("处理逾期记录成功");
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("处理逾期记录业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("处理逾期记录系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "处理逾期记录失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<BorrowStatsVO> getBorrowStats() {
        try {
            log.info("获取借阅统计请求");
            BorrowStatsVO stats = borrowService.getBorrowStats();
            return ApiResult.success(stats);
        } catch (BusinessException e) {
            log.error("获取借阅统计业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取借阅统计系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "获取借阅统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/my")
    public ApiResult<BorrowStatsVO> getMyBorrowStats() {
        try {            Long userId = SecurityUtils.getCurrentUserId();
            log.info("获取个人借阅统计请求, userId: {}", userId);
            BorrowStatsVO stats = borrowService.getUserBorrowStats(userId);
            return ApiResult.success(stats);
        } catch (BusinessException e) {
            log.error("获取个人借阅统计业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取个人借阅统计系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "获取个人借阅统计失败: " + e.getMessage());
        }
    }
}
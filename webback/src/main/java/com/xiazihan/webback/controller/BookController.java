package com.xiazihan.webback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiazihan.webback.common.api.ApiResult;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.model.dto.BookDTO;
import com.xiazihan.webback.model.dto.BookQueryDTO;
import com.xiazihan.webback.model.vo.BookVO;
import com.xiazihan.webback.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "图书管理", description = "图书相关接口")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @Operation(summary = "添加图书")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResult<Void> addBook(@Valid @RequestBody BookDTO bookDTO) {
        try {
            log.info("添加图书请求: {}", bookDTO);
            bookService.addBook(bookDTO);
            log.info("图书添加成功");
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("添加图书业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("添加图书系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "添加图书失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新图书")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResult<Void> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        try {
            log.info("更新图书请求, id: {}, data: {}", id, bookDTO);
            bookService.updateBook(id, bookDTO);
            log.info("图书更新成功, id: {}", id);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("更新图书业务异常, id: {}, error: {}", id, e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新图书系统异常, id: {}", id, e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "更新图书失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除图书")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResult<Void> deleteBook(@PathVariable Long id) {
        try {
            log.info("删除图书请求, id: {}", id);
            bookService.deleteBook(id);
            log.info("图书删除成功, id: {}", id);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("删除图书业务异常, id: {}, error: {}", id, e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除图书系统异常, id: {}", id, e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "删除图书失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取图书详情")
    @GetMapping("/{id}")
    public ApiResult<BookVO> getBookDetail(@PathVariable Long id) {
        try {
            log.info("获取图书详情请求, id: {}", id);
            BookVO bookVO = bookService.getBookDetail(id);
            return ApiResult.success(bookVO);
        } catch (BusinessException e) {
            log.error("获取图书详情业务异常, id: {}, error: {}", id, e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取图书详情系统异常, id: {}", id, e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "获取图书详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分页查询图书")
    @GetMapping
    public ApiResult<IPage<BookVO>> getBookPage(BookQueryDTO queryDTO) {
        try {
            log.info("分页查询图书请求: {}", queryDTO);
            IPage<BookVO> page = bookService.getBookPage(queryDTO);
            return ApiResult.success(page);
        } catch (BusinessException e) {
            log.error("分页查询图书业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("分页查询图书系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "查询图书失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新图书状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResult<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            log.info("更新图书状态请求, id: {}, status: {}", id, status);
            bookService.updateStatus(id, status);
            log.info("图书状态更新成功, id: {}, status: {}", id, status);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("更新图书状态业务异常, id: {}, status: {}, error: {}", id, status, e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新图书状态系统异常, id: {}", id, e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "更新图书状态失败: " + e.getMessage());
        }
    }
}
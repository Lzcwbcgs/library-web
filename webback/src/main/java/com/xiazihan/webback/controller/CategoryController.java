package com.xiazihan.webback.controller;

import com.xiazihan.webback.common.api.ApiResult;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.model.dto.CategoryDTO;
import com.xiazihan.webback.model.vo.CategoryVO;
import com.xiazihan.webback.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            log.info("添加分类请求: {}", categoryDTO);
            categoryService.addCategory(categoryDTO);
            log.info("分类添加成功");
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("添加分类业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("添加分类系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "添加分类失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            log.info("更新分类请求, id: {}, data: {}", id, categoryDTO);
            categoryService.updateCategory(id, categoryDTO);
            log.info("分类更新成功, id: {}", id);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("更新分类业务异常, id: {}, error: {}", id, e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新分类系统异常, id: {}", id, e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "更新分类失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> deleteCategory(@PathVariable Long id) {
        try {
            log.info("删除分类请求, id: {}", id);
            categoryService.deleteCategory(id);
            log.info("分类删除成功, id: {}", id);
            return ApiResult.success();
        } catch (BusinessException e) {
            log.error("删除分类业务异常, id: {}, error: {}", id, e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除分类系统异常, id: {}", id, e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "删除分类失败: " + e.getMessage());
        }
    }

    @GetMapping("/tree")
    public ApiResult<List<CategoryVO>> getCategoryTree() {
        try {
            log.info("获取分类树请求");
            List<CategoryVO> categoryTree = categoryService.getCategoryTree();
            return ApiResult.success(categoryTree);
        } catch (BusinessException e) {
            log.error("获取分类树业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取分类树系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "获取分类树失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResult<List<CategoryVO>> getAllCategories() {
        try {
            log.info("获取所有分类请求");
            List<CategoryVO> categories = categoryService.getAllCategories();
            return ApiResult.success(categories);
        } catch (BusinessException e) {
            log.error("获取所有分类业务异常: {}", e.getMessage());
            return ApiResult.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取所有分类系统异常", e);
            return ApiResult.failed(ResultCode.FAILED.getCode(), "获取所有分类失败: " + e.getMessage());
        }
    }
}
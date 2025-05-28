package com.xiazihan.webback.service;

import com.xiazihan.webback.model.dto.CategoryDTO;
import com.xiazihan.webback.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    /**
     * 添加分类
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 更新分类
     */
    void updateCategory(Long id, CategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 获取分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取所有分类（包含图书数量）
     */
    List<CategoryVO> getAllCategories();

    /**
     * 获取指定分类及其所有子分类的ID
     */
    List<Long> getCategoryIdsByParentId(Long parentId);
}
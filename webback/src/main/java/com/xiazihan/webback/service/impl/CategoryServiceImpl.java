package com.xiazihan.webback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiazihan.webback.common.api.ResultCode;
import com.xiazihan.webback.common.exception.BusinessException;
import com.xiazihan.webback.mapper.BookMapper;
import com.xiazihan.webback.mapper.CategoryMapper;
import com.xiazihan.webback.model.dto.CategoryDTO;
import com.xiazihan.webback.model.entity.BookCategory;
import com.xiazihan.webback.model.entity.BookInfo;
import com.xiazihan.webback.model.vo.CategoryVO;
import com.xiazihan.webback.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(CategoryDTO categoryDTO) {
        // 检查分类名称是否已存在
        if (isCategoryNameExists(categoryDTO.getName(), categoryDTO.getParentId())) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "该分类名称已存在");
        }

        BookCategory category = new BookCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        BookCategory category = getCategoryById(id);
        
        // 检查分类名称是否已存在（排除自身）
        if (!category.getName().equals(categoryDTO.getName()) && 
            isCategoryNameExists(categoryDTO.getName(), categoryDTO.getParentId())) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "该分类名称已存在");
        }

        // 不能将分类设置为自己的子分类
        if (id.equals(categoryDTO.getParentId())) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "不能将分类设置为自己的子分类");
        }

        // 不能将分类设置为其子分类的子分类
        List<Long> childIds = getCategoryIdsByParentId(id);
        if (childIds.contains(categoryDTO.getParentId())) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "不能将分类设置为其子分类的子分类");
        }

        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 检查是否有子分类
        if (hasChildCategory(id)) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "该分类下有子分类，不能删除");
        }

        // 检查分类下是否有图书
        if (hasBooksInCategory(id)) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "该分类下有图书，不能删除");
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        return categoryMapper.selectCategoryTree();
    }

    @Override
    public List<CategoryVO> getAllCategories() {
        return categoryMapper.selectCategoryBookCount();
    }

    @Override
    public List<Long> getCategoryIdsByParentId(Long parentId) {
        return categoryMapper.selectCategoryIdsByParentId(parentId);
    }

    private BookCategory getCategoryById(Long id) {
        BookCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.FAILED.getCode(), "分类不存在");
        }
        return category;
    }

    private boolean isCategoryNameExists(String name, Long parentId) {
        return categoryMapper.selectCount(new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getName, name)
                .eq(BookCategory::getParentId, parentId)) > 0;
    }

    private boolean hasChildCategory(Long parentId) {
        return categoryMapper.selectCount(new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getParentId, parentId)) > 0;
    }

    private boolean hasBooksInCategory(Long categoryId) {
        return bookMapper.selectCount(new LambdaQueryWrapper<BookInfo>()
                .eq(BookInfo::getCategoryId, categoryId)) > 0;
    }
}
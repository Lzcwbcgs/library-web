package com.xiazihan.webback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiazihan.webback.model.entity.BookCategory;
import com.xiazihan.webback.model.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<BookCategory> {
    /**
     * 获取分类树形结构
     */
    List<CategoryVO> selectCategoryTree();

    /**
     * 获取指定分类及其所有子分类的ID
     */
    List<Long> selectCategoryIdsByParentId(@Param("parentId") Long parentId);

    /**
     * 统计各分类下的图书数量
     */
    List<CategoryVO> selectCategoryBookCount();
}
package com.xiazihan.webback.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private Integer sort;
    private List<CategoryVO> children;
    private Integer bookCount;  // 该分类下的图书数量
}
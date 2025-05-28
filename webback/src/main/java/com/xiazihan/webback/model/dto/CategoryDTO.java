package com.xiazihan.webback.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    private Long parentId;
    
    @NotNull(message = "排序号不能为空")
    private Integer sort;
} 
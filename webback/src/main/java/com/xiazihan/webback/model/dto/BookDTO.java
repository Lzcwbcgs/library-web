package com.xiazihan.webback.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookDTO {
    @NotBlank(message = "书名不能为空")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    private String author;
    
    @NotBlank(message = "出版社不能为空")
    private String publisher;
    
    @NotBlank(message = "ISBN不能为空")
    private String isbn;
    
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    @NotNull(message = "总库存不能为空")
    @Min(value = 0, message = "总库存不能小于0")
    private Integer totalStock;
    
    private String location;
    
    private String description;
}
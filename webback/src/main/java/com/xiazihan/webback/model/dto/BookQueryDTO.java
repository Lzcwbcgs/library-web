package com.xiazihan.webback.model.dto;

import lombok.Data;

@Data
public class BookQueryDTO {
    private String keyword;
    private String isbn;
    private String title;
    private String author;
    private Long categoryId;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
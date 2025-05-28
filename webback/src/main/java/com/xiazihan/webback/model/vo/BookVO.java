package com.xiazihan.webback.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookVO {
    private Long id;
    
    private String title;
    
    private String author;
    
    private String publisher;
    
    private String isbn;
    
    private Long categoryId;
    
    private String categoryName;
    
    private Integer totalStock;
    
    private Integer availableStock;
    
    private Integer status;
    
    private Integer borrowCount;
    
    private String location;
    
    private String description;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
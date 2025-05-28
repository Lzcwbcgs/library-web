package com.xiazihan.webback.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("book_info")
public class BookInfo extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String author;
    
    private String publisher;
    
    private LocalDate publishDate;
    
    private String isbn;
    
    private Long categoryId;
    
    private String coverUrl;
    
    private BigDecimal price;
    
    private Integer totalStock;
    
    private Integer availableStock;
    
    private Integer status;
    
    private Integer borrowCount;
    
    private String location;
    
    private String description;
} 
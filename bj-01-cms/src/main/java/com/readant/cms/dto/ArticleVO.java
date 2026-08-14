package com.readant.cms.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文章返回体
 */
@Data
public class ArticleVO {

    private Long id;
    private String title;
    private String summary;
    private String content;
    private Long categoryId;
    private String categoryName;
    private String coverImage;
    private Integer status;
    private String author;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

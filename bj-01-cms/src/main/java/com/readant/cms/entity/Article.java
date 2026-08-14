package com.readant.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文章实体
 */
@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章标题 */
    private String title;

    /** 文章摘要 */
    private String summary;

    /** 文章内容（富文本 HTML） */
    private String content;

    /** 所属栏目ID */
    private Long categoryId;

    /** 封面图URL */
    private String coverImage;

    /** 状态：0=草稿，1=已发布 */
    private Integer status;

    /** 作者 */
    private String author;

    /** 浏览量 */
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

package com.readant.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建文章请求体
 */
@Data
public class ArticleCreateReq {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不超过200字")
    private String title;

    @Size(max = 500, message = "摘要不超过500字")
    private String summary;

    private String content;

    private Long categoryId;

    private String coverImage;

    /** 状态：0=草稿，1=已发布 */
    private Integer status;

    @Size(max = 50, message = "作者名不超过50字")
    private String author;
}
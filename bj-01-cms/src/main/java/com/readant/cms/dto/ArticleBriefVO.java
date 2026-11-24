package com.readant.cms.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文章简要信息（上一篇 / 下一篇 / 相关文章），不含正文，列表与导航场景专用
 */
@Data
public class ArticleBriefVO {

    private Long id;
    private String title;
    private LocalDateTime createdAt;
}

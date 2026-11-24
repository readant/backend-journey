package com.readant.cms.dto;

import lombok.Data;

/**
 * 文章导航（上一篇 / 下一篇），文章详情页底部使用；任一侧无相邻文章时为 null
 */
@Data
public class ArticleNavVO {

    private ArticleBriefVO prev;
    private ArticleBriefVO next;
}

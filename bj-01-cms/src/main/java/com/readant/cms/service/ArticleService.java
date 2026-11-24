package com.readant.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.dto.ArticleBriefVO;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleNavVO;
import com.readant.cms.dto.ArticleVO;
import java.util.List;

/**
 * 文章 Service 接口
 */
public interface ArticleService {

    /**
     * 创建文章
     */
    ArticleVO create(ArticleCreateReq req);

    /**
     * 根据 ID 查询文章
     */
    ArticleVO getById(Long id);

    /**
     * 分页查询文章列表；keyword 非空时按标题或摘要模糊匹配
     */
    Page<ArticleVO> page(int pageNum, int pageSize, Integer status, Long categoryId, String keyword);

    /**
     * 查询相邻文章导航（上一篇 / 下一篇），按已发布文章的 id 顺序
     */
    ArticleNavVO navigation(Long id);

    /**
     * 查询同栏目的相关文章（不含自身），最多 limit 条
     */
    List<ArticleBriefVO> related(Long id, int limit);

    /**
     * 更新文章
     */
    ArticleVO update(Long id, ArticleCreateReq req);

    /**
     * 删除文章
     */
    void delete(Long id);
}

package com.readant.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleVO;

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
     * 分页查询文章列表
     */
    Page<ArticleVO> page(int pageNum, int pageSize, Integer status, Long categoryId);

    /**
     * 更新文章
     */
    ArticleVO update(Long id, ArticleCreateReq req);

    /**
     * 删除文章
     */
    void delete(Long id);
}
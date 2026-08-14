package com.readant.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.BusinessException;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleVO;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.Category;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import com.readant.cms.service.ArticleService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 文章 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public ArticleVO create(ArticleCreateReq req) {
        Article article = new Article();
        BeanUtils.copyProperties(req, article);

        if (article.getStatus() == null) {
            article.setStatus(0); // 默认为草稿
        }
        article.setViewCount(0);

        articleMapper.insert(article);
        log.info("创建文章: title={}", article.getTitle());
        return toVO(article);
    }

    @Override
    public ArticleVO getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        return toVO(article);
    }

    @Override
    public Page<ArticleVO> page(int pageNum, int pageSize, Integer status, Long categoryId) {
        // 构建查询条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(status != null, Article::getStatus, status)
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(Article::getCreatedAt);

        // 分页查询
        Page<Article> articlePage = articleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为 VO
        Page<ArticleVO> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        voPage.setRecords(articlePage.getRecords().stream().map(this::toVO).collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public ArticleVO update(Long id, ArticleCreateReq req) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        BeanUtils.copyProperties(req, article);
        article.setId(id);
        articleMapper.updateById(article);
        log.info("更新文章: id={}", id);

        return toVO(article);
    }

    @Override
    public void delete(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        articleMapper.deleteById(id);
        log.info("删除文章: id={}", id);
    }

    /**
     * 将实体转换为 VO（附带栏目名称）
     */
    private ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);

        // 查询栏目名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        return vo;
    }
}

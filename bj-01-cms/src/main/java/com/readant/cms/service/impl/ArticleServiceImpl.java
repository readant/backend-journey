package com.readant.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.BusinessException;
import com.readant.cms.common.Pages;
import com.readant.cms.dto.ArticleBriefVO;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleNavVO;
import com.readant.cms.dto.ArticleVO;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.Category;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import com.readant.cms.service.ArticleService;
import java.util.Collections;
import java.util.List;
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
    public Page<ArticleVO> page(int pageNum, int pageSize, Integer status, Long categoryId, String keyword) {
        // 构建查询条件
        String kw = keyword == null ? "" : keyword.trim();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(status != null, Article::getStatus, status)
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                // 关键词要同时命中标题或摘要之一，因此 OR 必须包在 and() 分组里，
                // 否则 or() 会把 status/categoryId 条件也带进 OR，越权查出未发布文章
                .and(!kw.isEmpty(), w -> w.like(Article::getTitle, kw).or().like(Article::getSummary, kw))
                .orderByDesc(Article::getCreatedAt);

        // 分页查询
        Page<Article> articlePage = articleMapper.selectPage(Pages.of(pageNum, pageSize), wrapper);

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

    @Override
    public ArticleNavVO navigation(Long id) {
        Article current = articleMapper.selectById(id);
        if (current == null) {
            throw new BusinessException(404, "文章不存在");
        }
        ArticleNavVO nav = new ArticleNavVO();
        // 相邻关系按 id 排序：自增主键与创建顺序一致，与列表的 createdAt 排序同向
        Article prev = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .lt(Article::getId, id)
                .orderByDesc(Article::getId)
                .last("LIMIT 1"));
        nav.setPrev(prev == null ? null : toBrief(prev));
        Article next = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .gt(Article::getId, id)
                .orderByAsc(Article::getId)
                .last("LIMIT 1"));
        nav.setNext(next == null ? null : toBrief(next));
        return nav;
    }

    @Override
    public List<ArticleBriefVO> related(Long id, int limit) {
        Article current = articleMapper.selectById(id);
        if (current == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (current.getCategoryId() == null) {
            return Collections.emptyList();
        }
        // 上限 10 条，防止前端传大值拖库；返回值是纯数字，无注入风险
        int safeLimit = normalizeRelatedLimit(limit);
        List<Article> list = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .eq(Article::getCategoryId, current.getCategoryId())
                .ne(Article::getId, id)
                .orderByDesc(Article::getCreatedAt)
                .last("LIMIT " + safeLimit));
        return list.stream().map(this::toBrief).collect(Collectors.toList());
    }

    /**
     * 相关文章条数收敛：下限 1、上限 10。包级可见以便单元测试直接验证边界
     */
    static int normalizeRelatedLimit(int limit) {
        return Math.max(1, Math.min(limit, 10));
    }

    /**
     * 将实体转换为简要 VO（导航与相关文章场景，不含正文）
     */
    private ArticleBriefVO toBrief(Article article) {
        ArticleBriefVO brief = new ArticleBriefVO();
        brief.setId(article.getId());
        brief.setTitle(article.getTitle());
        brief.setCreatedAt(article.getCreatedAt());
        return brief;
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

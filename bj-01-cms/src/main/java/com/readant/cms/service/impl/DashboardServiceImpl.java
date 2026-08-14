package com.readant.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.Product;
import com.readant.cms.mapper.AdminMapper;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import com.readant.cms.mapper.ProductMapper;
import com.readant.cms.service.DashboardService;
import com.readant.cms.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据看板统计 Service 实现
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AdminMapper adminMapper;
    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final ProductMapper productMapper;
    private final ProductCategoryService productCategoryService;

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> data = new HashMap<>();

        // 基础统计卡片
        data.put("adminCount", adminMapper.selectCount(null));
        data.put("categoryCount", categoryMapper.selectCount(null));
        data.put("articleCount", articleMapper.selectCount(null));
        data.put("productCount", productMapper.selectCount(null));

        // 文章状态分布（草稿/已发布）
        data.put("articleStatus", buildArticleStatus());

        // 产品分类分布（按顶级分类统计产品数）
        data.put("productByCategory", buildProductByCategory());

        // 最近 7 天文章发布趋势
        data.put("recentArticleTrend", buildRecentArticleTrend());

        return data;
    }

    private Map<String, Object> buildArticleStatus() {
        Map<String, Object> articleStatus = new HashMap<>();
        articleStatus.put("draft", articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 0)));
        articleStatus.put("published", articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)));
        return articleStatus;
    }

    private Map<String, Object> buildProductByCategory() {
        Map<String, Object> productByCategory = new HashMap<>();
        productCategoryService.getTree().forEach(cat -> {
            Long count = productMapper.selectCount(
                    new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, cat.getId()));
            productByCategory.put(cat.getName(), count);
        });
        return productByCategory;
    }

    private Map<String, Object> buildRecentArticleTrend() {
        Map<String, Object> result = new HashMap<>();
        List<Article> articles = articleMapper.selectList(null);
        Map<String, Long> byDay = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            String key = today.minusDays(i).toString();
            byDay.put(key, 0L);
        }
        for (Article article : articles) {
            if (article.getCreatedAt() != null) {
                String key = article.getCreatedAt().toLocalDate().toString();
                byDay.computeIfPresent(key, (k, v) -> v + 1);
            }
        }
        result.put("days", byDay.keySet().stream().sorted().toList());
        result.put("counts", byDay.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue).toList());
        return result;
    }
}
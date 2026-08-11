package com.readant.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.readant.cms.common.R;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.Category;
import com.readant.cms.entity.Product;
import com.readant.cms.mapper.AdminMapper;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import com.readant.cms.mapper.ProductMapper;
import com.readant.cms.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据看板统计接口
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AdminMapper adminMapper;
    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final ProductMapper productMapper;
    private final ProductCategoryService productCategoryService;

    /**
     * 数据看板统计
     */
    @GetMapping
    public R<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        // 基础统计卡片
        data.put("adminCount", adminMapper.selectCount(null));
        data.put("categoryCount", categoryMapper.selectCount(null));
        data.put("articleCount", articleMapper.selectCount(null));
        data.put("productCount", productMapper.selectCount(null));

        // 文章状态分布（草稿/已发布）
        Map<String, Object> articleStatus = new HashMap<>();
        articleStatus.put("draft", articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 0)));
        articleStatus.put("published", articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)));
        data.put("articleStatus", articleStatus);

        // 产品分类分布（按顶级分类统计产品数）
        Map<String, Object> productByCategory = new HashMap<>();
        productCategoryService.getTree().forEach(cat -> {
            Long count = productMapper.selectCount(
                    new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, cat.getId()));
            productByCategory.put(cat.getName(), count);
        });
        data.put("productByCategory", productByCategory);

        // 最近 7 天文章发布趋势
        data.put("recentArticleTrend", buildRecentArticleTrend());

        return R.success(data);
    }

    private Map<String, Object> buildRecentArticleTrend() {
        Map<String, Object> result = new HashMap<>();
        List<Article> articles = articleMapper.selectList(null);
        Map<String, Long> byDay = new HashMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();

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
package com.readant.cms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.ProductCategory;
import com.readant.cms.mapper.AdminMapper;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import com.readant.cms.mapper.ProductMapper;
import com.readant.cms.service.ProductCategoryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DashboardServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductCategoryService productCategoryService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Nested
    @DisplayName("stats 方法")
    class Stats {

        @Test
        @DisplayName("聚合统计 → 返回完整看板数据")
        void shouldReturnFullStats() {
            // given：基础计数
            when(adminMapper.selectCount(null)).thenReturn(2L);
            when(categoryMapper.selectCount(null)).thenReturn(5L);
            when(articleMapper.selectCount(null)).thenReturn(10L);
            when(productMapper.selectCount(null)).thenReturn(6L);
            // 文章状态分布：3 草稿 + 7 已发布
            when(articleMapper.selectCount(any(Wrapper.class))).thenReturn(3L, 7L);
            // 产品分类：2 个顶级分类，各关联产品数
            ProductCategory cat1 = new ProductCategory();
            cat1.setId(1L);
            cat1.setName("机械");
            ProductCategory cat2 = new ProductCategory();
            cat2.setId(2L);
            cat2.setName("电子");
            when(productCategoryService.getTree()).thenReturn(List.of(cat1, cat2));
            when(productMapper.selectCount(any(Wrapper.class))).thenReturn(2L, 4L);
            // 近 7 天趋势：今天发布 1 篇
            Article today = new Article();
            today.setCreatedAt(LocalDateTime.now());
            when(articleMapper.selectList(null)).thenReturn(List.of(today));

            // when
            Map<String, Object> data = dashboardService.stats();

            // then
            assertThat(data.get("adminCount")).isEqualTo(2L);
            assertThat(data.get("categoryCount")).isEqualTo(5L);
            assertThat(data.get("articleCount")).isEqualTo(10L);
            assertThat(data.get("productCount")).isEqualTo(6L);

            @SuppressWarnings("unchecked")
            Map<String, Object> articleStatus = (Map<String, Object>) data.get("articleStatus");
            assertThat(articleStatus.get("draft")).isEqualTo(3L);
            assertThat(articleStatus.get("published")).isEqualTo(7L);

            @SuppressWarnings("unchecked")
            Map<String, Object> productByCategory = (Map<String, Object>) data.get("productByCategory");
            assertThat(productByCategory).containsEntry("机械", 2L).containsEntry("电子", 4L);

            @SuppressWarnings("unchecked")
            Map<String, Object> trend = (Map<String, Object>) data.get("recentArticleTrend");
            assertThat((List<?>) trend.get("days")).hasSize(7);
            assertThat((List<?>) trend.get("counts")).hasSize(7);
        }

        @Test
        @DisplayName("无产品分类 → 分类分布为空 Map")
        void shouldReturnEmptyProductByCategory_whenNoCategories() {
            when(adminMapper.selectCount(null)).thenReturn(0L);
            when(categoryMapper.selectCount(null)).thenReturn(0L);
            when(articleMapper.selectCount(null)).thenReturn(0L);
            when(productMapper.selectCount(null)).thenReturn(0L);
            when(articleMapper.selectCount(any(Wrapper.class))).thenReturn(0L, 0L);
            when(productCategoryService.getTree()).thenReturn(List.of());
            when(articleMapper.selectList(null)).thenReturn(List.of());

            Map<String, Object> data = dashboardService.stats();

            @SuppressWarnings("unchecked")
            Map<String, Object> productByCategory = (Map<String, Object>) data.get("productByCategory");
            assertThat(productByCategory).isEmpty();
            // 各统计维度仍正常返回
            assertThat(data).containsKeys("adminCount", "articleStatus", "recentArticleTrend");
            verify(productCategoryService).getTree();
        }
    }
}

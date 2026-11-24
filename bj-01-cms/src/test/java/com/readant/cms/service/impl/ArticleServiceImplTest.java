package com.readant.cms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.BusinessException;
import com.readant.cms.dto.ArticleBriefVO;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleNavVO;
import com.readant.cms.dto.ArticleVO;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.Category;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ArticleServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    /**
     * LambdaQueryWrapper 解析 lambda 到列名依赖 MP 的表信息缓存（通常由容器启动时注册 Mapper 建立）。
     * 纯单元测试环境手动初始化 Article 的缓存，否则断言 getSqlSegment 会抛
     * "can not find lambda cache"。
     */
    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Article.class);
    }

    private Article buildArticle(Long id, String title, Integer status, Long categoryId) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setStatus(status);
        article.setCategoryId(categoryId);
        return article;
    }

    private ArticleCreateReq buildReq(String title, Integer status) {
        ArticleCreateReq req = new ArticleCreateReq();
        req.setTitle(title);
        req.setStatus(status);
        return req;
    }

    @Nested
    @DisplayName("create 方法")
    class Create {

        @Test
        @DisplayName("未指定状态 → 默认为草稿(0)且浏览量初始为0")
        void shouldDefaultToDraft_whenStatusNull() {
            ArticleCreateReq req = buildReq("标题", null);

            articleService.create(req);

            ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
            verify(articleMapper).insert(captor.capture());
            Article saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(0);
            assertThat(saved.getViewCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("指定状态 → 按传入状态保存")
        void shouldKeepStatus_whenStatusProvided() {
            ArticleCreateReq req = buildReq("标题", 1);

            articleService.create(req);

            ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
            verify(articleMapper).insert(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getById 方法")
    class GetById {

        @Test
        @DisplayName("文章存在 → 返回 VO 并附带栏目名称")
        void shouldReturnArticle_whenExists() {
            Article article = buildArticle(1L, "标题", 1, 10L);
            when(articleMapper.selectById(1L)).thenReturn(article);
            Category category = new Category();
            category.setId(10L);
            category.setName("公司新闻");
            when(categoryMapper.selectById(10L)).thenReturn(category);

            ArticleVO result = articleService.getById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCategoryName()).isEqualTo("公司新闻");
        }

        @Test
        @DisplayName("文章不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(articleMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> articleService.getById(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("文章不存在");
        }
    }

    @Nested
    @DisplayName("page 方法")
    class PageQuery {

        @Test
        @DisplayName("无栏目ID → 栏目名称为空")
        void shouldReturnPage_whenNoCategory() {
            Article article = buildArticle(1L, "标题", 1, null);
            Page<Article> articlePage = new Page<>(1, 10, 1);
            articlePage.setRecords(Collections.singletonList(article));
            when(articleMapper.selectPage(any(Page.class), any())).thenReturn(articlePage);

            Page<ArticleVO> result = articleService.page(1, 10, 1, null, null);

            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getCategoryName()).isNull();
        }

        @Test
        @DisplayName("带关键词 → 标题与摘要进入 LIKE 条件")
        void shouldApplyKeyword_whenProvided() {
            Page<Article> articlePage = new Page<>(1, 10, 0);
            articlePage.setRecords(Collections.emptyList());
            when(articleMapper.selectPage(any(Page.class), any())).thenReturn(articlePage);

            articleService.page(1, 10, 1, null, "Vue");

            @SuppressWarnings("unchecked")
            ArgumentCaptor<LambdaQueryWrapper<Article>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
            verify(articleMapper).selectPage(any(Page.class), captor.capture());
            LambdaQueryWrapper<Article> wrapper = captor.getValue();
            // OR 必须出现在分组括号内：验证标题与摘要之一命中即算匹配，且不破坏 status/category 条件
            assertThat(wrapper.getSqlSegment()).contains("LIKE");
            assertThat(wrapper.getSqlSegment()).contains(" OR ");
        }

        @Test
        @DisplayName("带栏目ID且栏目存在 → 附带栏目名称")
        void shouldSetCategoryName_whenCategoryExists() {
            Article article = buildArticle(1L, "标题", 1, 10L);
            Page<Article> articlePage = new Page<>(1, 10, 1);
            articlePage.setRecords(Collections.singletonList(article));
            when(articleMapper.selectPage(any(Page.class), any())).thenReturn(articlePage);
            Category category = new Category();
            category.setId(10L);
            category.setName("公司新闻");
            when(categoryMapper.selectById(10L)).thenReturn(category);

            Page<ArticleVO> result = articleService.page(1, 10, 1, 10L, null);

            assertThat(result.getRecords().get(0).getCategoryName()).isEqualTo("公司新闻");
        }
    }

    @Nested
    @DisplayName("navigation 方法")
    class Navigation {

        @Test
        @DisplayName("两侧都有相邻文章 → 返回 prev 与 next")
        void shouldReturnBoth_whenNeighborsExist() {
            when(articleMapper.selectById(5L)).thenReturn(buildArticle(5L, "当前", 1, 10L));
            when(articleMapper.selectOne(any()))
                    .thenReturn(buildArticle(4L, "上一篇", 1, 10L), buildArticle(6L, "下一篇", 1, 10L));

            ArticleNavVO nav = articleService.navigation(5L);

            assertThat(nav.getPrev().getId()).isEqualTo(4L);
            assertThat(nav.getNext().getId()).isEqualTo(6L);
        }

        @Test
        @DisplayName("首篇无上一篇 → prev 为 null")
        void shouldReturnNullPrev_whenFirstArticle() {
            when(articleMapper.selectById(1L)).thenReturn(buildArticle(1L, "当前", 1, 10L));
            when(articleMapper.selectOne(any())).thenReturn(null, buildArticle(2L, "下一篇", 1, 10L));

            ArticleNavVO nav = articleService.navigation(1L);

            assertThat(nav.getPrev()).isNull();
            assertThat(nav.getNext().getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("文章不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(articleMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> articleService.navigation(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("文章不存在");
        }
    }

    @Nested
    @DisplayName("related 方法")
    class Related {

        @Test
        @DisplayName("文章不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(articleMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> articleService.related(9L, 5))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("文章不存在");
        }

        @Test
        @DisplayName("无栏目 → 返回空列表且不查询")
        void shouldReturnEmpty_whenNoCategory() {
            when(articleMapper.selectById(1L)).thenReturn(buildArticle(1L, "当前", 1, null));

            assertThat(articleService.related(1L, 5)).isEmpty();
            verify(articleMapper, never()).selectList(any());
        }

        @Test
        @DisplayName("同栏目文章 → 映射为简要 VO")
        void shouldReturnBriefs_whenSameCategory() {
            when(articleMapper.selectById(5L)).thenReturn(buildArticle(5L, "当前", 1, 10L));
            Article a = buildArticle(3L, "相关一", 1, 10L);
            a.setCreatedAt(LocalDateTime.of(2026, 1, 2, 0, 0));
            Article b = buildArticle(4L, "相关二", 1, 10L);
            b.setCreatedAt(LocalDateTime.of(2026, 1, 3, 0, 0));
            when(articleMapper.selectList(any())).thenReturn(List.of(a, b));

            List<ArticleBriefVO> result = articleService.related(5L, 5);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(3L);
            assertThat(result.get(0).getTitle()).isEqualTo("相关一");
        }

        @Test
        @DisplayName("limit 超过上限 → 收敛到 10")
        void shouldClampLimit_whenExceeded() {
            when(articleMapper.selectById(5L)).thenReturn(buildArticle(5L, "当前", 1, 10L));
            when(articleMapper.selectList(any())).thenReturn(Collections.emptyList());

            articleService.related(5L, 99);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<LambdaQueryWrapper<Article>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
            verify(articleMapper).selectList(captor.capture());
            assertThat(captor.getValue().getSqlSegment()).contains("LIMIT 10");
        }

        @Test
        @DisplayName("limit 收敛函数：下限 1、上限 10")
        void shouldNormalizeLimit() {
            assertThat(ArticleServiceImpl.normalizeRelatedLimit(99)).isEqualTo(10);
            assertThat(ArticleServiceImpl.normalizeRelatedLimit(10)).isEqualTo(10);
            assertThat(ArticleServiceImpl.normalizeRelatedLimit(5)).isEqualTo(5);
            assertThat(ArticleServiceImpl.normalizeRelatedLimit(0)).isEqualTo(1);
            assertThat(ArticleServiceImpl.normalizeRelatedLimit(-5)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("update 方法")
    class Update {

        @Test
        @DisplayName("文章不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(articleMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> articleService.update(9L, buildReq("标题", 1)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("文章不存在");
            verify(articleMapper, never()).updateById(any(Article.class));
        }

        @Test
        @DisplayName("文章存在 → 更新成功")
        void shouldUpdate_whenExists() {
            Article article = buildArticle(1L, "旧标题", 0, null);
            when(articleMapper.selectById(1L)).thenReturn(article);

            ArticleVO result = articleService.update(1L, buildReq("新标题", 1));

            assertThat(result.getTitle()).isEqualTo("新标题");
            assertThat(result.getStatus()).isEqualTo(1);
            verify(articleMapper).updateById(any(Article.class));
        }
    }

    @Nested
    @DisplayName("delete 方法")
    class Delete {

        @Test
        @DisplayName("文章不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(articleMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> articleService.delete(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("文章不存在");
            verify(articleMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("文章存在 → 删除成功")
        void shouldDelete_whenExists() {
            when(articleMapper.selectById(1L)).thenReturn(buildArticle(1L, "标题", 1, null));

            articleService.delete(1L);

            verify(articleMapper).deleteById(1L);
        }
    }
}

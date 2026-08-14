package com.readant.cms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.BusinessException;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleVO;
import com.readant.cms.entity.Article;
import com.readant.cms.entity.Category;
import com.readant.cms.mapper.ArticleMapper;
import com.readant.cms.mapper.CategoryMapper;
import java.util.Collections;
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

            Page<ArticleVO> result = articleService.page(1, 10, 1, null);

            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getCategoryName()).isNull();
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

            Page<ArticleVO> result = articleService.page(1, 10, 1, 10L);

            assertThat(result.getRecords().get(0).getCategoryName()).isEqualTo("公司新闻");
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

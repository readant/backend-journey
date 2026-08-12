package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.Category;
import com.readant.cms.mapper.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CategoryServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category buildCategory(Long id, String name, Long parentId) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setParentId(parentId);
        return category;
    }

    @Nested
    @DisplayName("getTree 方法")
    class GetTree {

        @Test
        @DisplayName("平铺数据 → 组装成树形结构")
        void shouldBuildTree() {
            Category root = buildCategory(1L, "关于我们", null);
            Category child = buildCategory(2L, "公司简介", 1L);
            Category orphan = buildCategory(3L, "无父级", 99L);
            when(categoryMapper.selectList(null)).thenReturn(Arrays.asList(root, child, orphan));

            List<Category> tree = categoryService.getTree();

            // 顶级节点只有 parentId 为 null 的节点
            assertThat(tree).hasSize(1);
            assertThat(tree.get(0).getId()).isEqualTo(1L);
            // 顶级节点包含子节点
            assertThat(tree.get(0).getChildren()).hasSize(1);
            assertThat(tree.get(0).getChildren().get(0).getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("无数据 → 返回空列表")
        void shouldReturnEmpty_whenNoData() {
            when(categoryMapper.selectList(null)).thenReturn(List.of());

            List<Category> tree = categoryService.getTree();

            assertThat(tree).isEmpty();
        }
    }

    @Nested
    @DisplayName("create 方法")
    class Create {

        @Test
        @DisplayName("顶级栏目 → 默认填充排序和启用状态")
        void shouldCreateTopLevel() {
            Category category = buildCategory(null, "关于我们", null);

            categoryService.create(category);

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryMapper).insert(captor.capture());
            assertThat(captor.getValue().getSortOrder()).isEqualTo(0);
            assertThat(captor.getValue().getStatus()).isEqualTo(1);
        }

        @Test
        @DisplayName("父栏目存在 → 创建成功")
        void shouldCreate_whenParentExists() {
            when(categoryMapper.selectById(1L)).thenReturn(buildCategory(1L, "父", null));
            Category category = buildCategory(null, "子栏目", 1L);

            categoryService.create(category);

            verify(categoryMapper).insert(any(Category.class));
        }

        @Test
        @DisplayName("父栏目不存在 → 抛出业务异常 400")
        void shouldThrow_whenParentMissing() {
            when(categoryMapper.selectById(5L)).thenReturn(null);
            Category category = buildCategory(null, "子栏目", 5L);

            assertThatThrownBy(() -> categoryService.create(category))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("父栏目不存在");
            verify(categoryMapper, never()).insert(any(Category.class));
        }
    }

    @Nested
    @DisplayName("update 方法")
    class Update {

        @Test
        @DisplayName("栏目不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(categoryMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> categoryService.update(9L, buildCategory(null, "x", null)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("栏目不存在");
        }

        @Test
        @DisplayName("栏目存在 → 更新成功并返回最新数据")
        void shouldUpdate_whenExists() {
            when(categoryMapper.selectById(1L)).thenReturn(buildCategory(1L, "旧", null));
            Category updated = buildCategory(1L, "新名称", null);
            when(categoryMapper.selectById(1L)).thenReturn(updated);

            Category result = categoryService.update(1L, buildCategory(null, "新名称", null));

            assertThat(result.getName()).isEqualTo("新名称");
            verify(categoryMapper).updateById(any(Category.class));
        }
    }

    @Nested
    @DisplayName("delete 方法")
    class Delete {

        @Test
        @DisplayName("存在子栏目 → 抛出业务异常 400")
        void shouldThrow_whenHasChildren() {
            Category root = buildCategory(1L, "根", null);
            Category child = buildCategory(2L, "子", 1L);
            when(categoryMapper.selectList(null)).thenReturn(Arrays.asList(root, child));

            assertThatThrownBy(() -> categoryService.delete(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("该栏目下有子栏目，请先删除子栏目");
            verify(categoryMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("无子栏目 → 删除成功")
        void shouldDelete_whenNoChildren() {
            when(categoryMapper.selectList(null)).thenReturn(List.of(buildCategory(1L, "根", null)));

            categoryService.delete(1L);

            verify(categoryMapper).deleteById(1L);
        }
    }
}
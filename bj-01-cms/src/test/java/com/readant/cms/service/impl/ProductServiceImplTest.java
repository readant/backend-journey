package com.readant.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.Product;
import com.readant.cms.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ProductServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product buildProduct(Long id, String name, Long categoryId) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setPrice(new BigDecimal("99.00"));
        return product;
    }

    @Nested
    @DisplayName("create 方法")
    class Create {

        @Test
        @DisplayName("未指定状态 → 默认草稿(0)")
        void shouldDefaultToDraft_whenStatusNull() {
            Product product = buildProduct(null, "新产品", 1L);

            productService.create(product);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productMapper).insert(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(0);
        }

        @Test
        @DisplayName("指定状态 → 按传入状态保存")
        void shouldKeepStatus_whenProvided() {
            Product product = buildProduct(null, "新品", 1L);
            product.setStatus(1);

            productService.create(product);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productMapper).insert(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getById 方法")
    class GetById {

        @Test
        @DisplayName("产品存在 → 返回产品")
        void shouldReturnProduct_whenExists() {
            when(productMapper.selectById(1L)).thenReturn(buildProduct(1L, "产品", 1L));

            Product result = productService.getById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("产品");
        }

        @Test
        @DisplayName("产品不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(productMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> productService.getById(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("产品不存在");
        }
    }

    @Nested
    @DisplayName("page 方法")
    class PageQuery {

        @Test
        @DisplayName("分页查询 → 返回分页结果")
        void shouldReturnPage() {
            Page<Product> productPage = new Page<>(1, 10, 1);
            productPage.setRecords(java.util.List.of(buildProduct(1L, "产品", 1L)));
            when(productMapper.selectPage(any(Page.class), any(Wrapper.class)))
                    .thenReturn(productPage);

            Page<Product> result = productService.page(1, 10, 1L);

            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getCategoryId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("无栏目ID → 查询所有产品")
        void shouldQueryAll_whenNoCategory() {
            Page<Product> productPage = new Page<>(1, 10, 0);
            when(productMapper.selectPage(any(Page.class), any(Wrapper.class)))
                    .thenReturn(productPage);

            Page<Product> result = productService.page(1, 10, null);

            assertThat(result.getTotal()).isZero();
            verify(productMapper).selectPage(any(Page.class), any(Wrapper.class));
        }
    }

    @Nested
    @DisplayName("update 方法")
    class Update {

        @Test
        @DisplayName("产品不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(productMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> productService.update(9L, buildProduct(null, "x", 1L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("产品不存在");
            verify(productMapper, never()).updateById(any(Product.class));
        }

        @Test
        @DisplayName("产品存在 → 更新成功并返回最新数据")
        void shouldUpdate_whenExists() {
            Product updated = buildProduct(1L, "新名称", 1L);
            when(productMapper.selectById(1L)).thenReturn(buildProduct(1L, "旧名称", 1L), updated);

            Product result = productService.update(1L, buildProduct(null, "新名称", 1L));

            assertThat(result.getName()).isEqualTo("新名称");
            verify(productMapper).updateById(any(Product.class));
        }
    }

    @Nested
    @DisplayName("delete 方法")
    class Delete {

        @Test
        @DisplayName("产品不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(productMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> productService.delete(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("产品不存在");
            verify(productMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("产品存在 → 删除成功")
        void shouldDelete_whenExists() {
            when(productMapper.selectById(1L)).thenReturn(buildProduct(1L, "产品", 1L));

            productService.delete(1L);

            verify(productMapper).deleteById(1L);
        }
    }
}
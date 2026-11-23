package com.readant.cms.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pages 分页兜底单元测试")
class PagesTest {

    @Test
    @DisplayName("合法参数原样保留")
    void shouldKeepValidParams() {
        Page<Object> page = Pages.of(2, 20);

        assertThat(page.getCurrent()).isEqualTo(2);
        assertThat(page.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("超大 pageSize 被截断到上限")
    void shouldCapOversizedPageSize() {
        assertThat(Pages.of(1, 1_000_000).getSize()).isEqualTo(Pages.MAX_PAGE_SIZE);
        assertThat(Pages.of(1, Pages.MAX_PAGE_SIZE + 1).getSize()).isEqualTo(Pages.MAX_PAGE_SIZE);
    }

    @Test
    @DisplayName("非法 pageSize 回落到默认值")
    void shouldFallbackWhenSizeInvalid() {
        assertThat(Pages.of(1, 0).getSize()).isEqualTo(Pages.DEFAULT_PAGE_SIZE);
        assertThat(Pages.of(1, -5).getSize()).isEqualTo(Pages.DEFAULT_PAGE_SIZE);
    }

    @Test
    @DisplayName("页码小于 1 归到第 1 页")
    void shouldClampPageNum() {
        assertThat(Pages.of(0, 10).getCurrent()).isEqualTo(1);
        assertThat(Pages.of(-3, 10).getCurrent()).isEqualTo(1);
    }
}

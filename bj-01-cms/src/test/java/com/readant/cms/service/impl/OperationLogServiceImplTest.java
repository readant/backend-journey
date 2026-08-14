package com.readant.cms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.entity.OperationLog;
import com.readant.cms.mapper.OperationLogMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("OperationLogServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class OperationLogServiceImplTest {

    @Mock
    private OperationLogMapper operationLogMapper;

    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    private OperationLog buildLog(Long id, String module, String action) {
        OperationLog log = new OperationLog();
        log.setId(id);
        log.setModule(module);
        log.setAction(action);
        return log;
    }

    @Nested
    @DisplayName("page 方法")
    class PageQuery {

        @Test
        @DisplayName("无筛选条件 → 查询全部分页日志")
        void shouldQueryAll_whenNoFilter() {
            Page<OperationLog> logPage = new Page<>(1, 10, 1);
            logPage.setRecords(List.of(buildLog(1L, "article", "create")));
            when(operationLogMapper.selectPage(any(Page.class), any(Wrapper.class)))
                    .thenReturn(logPage);

            Page<OperationLog> result = operationLogService.page(1, 10, null, null);

            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getModule()).isEqualTo("article");
        }

        @Test
        @DisplayName("带模块和动作筛选 → 按条件过滤")
        void shouldFilterByModuleAndAction() {
            Page<OperationLog> logPage = new Page<>(1, 10, 1);
            logPage.setRecords(List.of(buildLog(2L, "article", "update")));
            when(operationLogMapper.selectPage(any(Page.class), any(Wrapper.class)))
                    .thenReturn(logPage);

            Page<OperationLog> result = operationLogService.page(1, 10, "article", "update");

            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getAction()).isEqualTo("update");
            verify(operationLogMapper).selectPage(any(Page.class), any(Wrapper.class));
        }

        @Test
        @DisplayName("模块为空白字符串 → 视为无筛选条件")
        void shouldIgnoreBlankModule() {
            Page<OperationLog> logPage = new Page<>(1, 10, 0);
            when(operationLogMapper.selectPage(any(Page.class), any(Wrapper.class)))
                    .thenReturn(logPage);

            operationLogService.page(1, 10, "  ", "update");

            // 空白模块不应作为筛选条件，仍然执行分页
            verify(operationLogMapper).selectPage(any(Page.class), any(Wrapper.class));
        }
    }
}

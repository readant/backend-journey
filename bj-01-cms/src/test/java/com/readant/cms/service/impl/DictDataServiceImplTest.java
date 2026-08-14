package com.readant.cms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.DictData;
import com.readant.cms.mapper.DictDataMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DictDataServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class DictDataServiceImplTest {

    @Mock
    private DictDataMapper dictDataMapper;

    @InjectMocks
    private DictDataServiceImpl dictDataService;

    private DictData buildDictData(Long id, String dictType, String dictCode) {
        DictData dictData = new DictData();
        dictData.setId(id);
        dictData.setDictType(dictType);
        dictData.setDictCode(dictCode);
        return dictData;
    }

    @Nested
    @DisplayName("getByType 方法")
    class GetByType {

        @Test
        @DisplayName("按类型查询 → 仅返回启用项且按排序")
        void shouldReturnEnabledOrderedItems() {
            DictData item = buildDictData(1L, "ARTICLE_STATUS", "PUBLISHED");
            when(dictDataMapper.selectList(any(Wrapper.class))).thenReturn(List.of(item));

            List<DictData> result = dictDataService.getByType("ARTICLE_STATUS");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDictCode()).isEqualTo("PUBLISHED");
            verify(dictDataMapper).selectList(any(Wrapper.class));
        }

        @Test
        @DisplayName("无匹配数据 → 返回空列表")
        void shouldReturnEmpty_whenNoMatch() {
            when(dictDataMapper.selectList(any(Wrapper.class))).thenReturn(List.of());

            List<DictData> result = dictDataService.getByType("NO_SUCH_TYPE");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create 方法")
    class Create {

        @Test
        @DisplayName("未指定排序和状态 → 默认填充 0 和启用")
        void shouldFillDefaults_whenNull() {
            DictData dictData = buildDictData(null, "TYPE", "CODE");

            dictDataService.create(dictData);

            ArgumentCaptor<DictData> captor = ArgumentCaptor.forClass(DictData.class);
            verify(dictDataMapper).insert(captor.capture());
            assertThat(captor.getValue().getSortOrder()).isEqualTo(0);
            assertThat(captor.getValue().getStatus()).isEqualTo(1);
        }

        @Test
        @DisplayName("指定排序和状态 → 按传入值保存")
        void shouldKeepProvidedValues() {
            DictData dictData = buildDictData(null, "TYPE", "CODE");
            dictData.setSortOrder(3);
            dictData.setStatus(0);

            dictDataService.create(dictData);

            ArgumentCaptor<DictData> captor = ArgumentCaptor.forClass(DictData.class);
            verify(dictDataMapper).insert(captor.capture());
            assertThat(captor.getValue().getSortOrder()).isEqualTo(3);
            assertThat(captor.getValue().getStatus()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("update 方法")
    class Update {

        @Test
        @DisplayName("字典项不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(dictDataMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> dictDataService.update(9L, buildDictData(null, "T", "C")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("字典项不存在");
            verify(dictDataMapper, never()).updateById(any(DictData.class));
        }

        @Test
        @DisplayName("字典项存在 → 更新成功并返回最新数据")
        void shouldUpdate_whenExists() {
            DictData existing = buildDictData(1L, "OLD", "OLD_CODE");
            DictData updated = buildDictData(1L, "NEW", "NEW_CODE");
            when(dictDataMapper.selectById(1L)).thenReturn(existing, updated);

            DictData result = dictDataService.update(1L, buildDictData(null, "NEW", "NEW_CODE"));

            assertThat(result.getDictType()).isEqualTo("NEW");
            verify(dictDataMapper).updateById(any(DictData.class));
        }
    }

    @Nested
    @DisplayName("delete 方法")
    class Delete {

        @Test
        @DisplayName("字典项不存在 → 抛出业务异常 404")
        void shouldThrow_whenNotFound() {
            when(dictDataMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> dictDataService.delete(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("字典项不存在");
            verify(dictDataMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("字典项存在 → 删除成功")
        void shouldDelete_whenExists() {
            when(dictDataMapper.selectById(1L)).thenReturn(buildDictData(1L, "T", "C"));

            dictDataService.delete(1L);

            verify(dictDataMapper).deleteById(1L);
        }
    }
}

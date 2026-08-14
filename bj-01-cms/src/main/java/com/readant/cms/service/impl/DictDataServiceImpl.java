package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.DictData;
import com.readant.cms.mapper.DictDataMapper;
import com.readant.cms.service.DictDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private final DictDataMapper dictDataMapper;

    @Override
    public List<DictData> getByType(String dictType) {
        return dictDataMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictData>()
                        .eq(DictData::getDictType, dictType)
                        .eq(DictData::getStatus, 1)
                        .orderByAsc(DictData::getSortOrder));
    }

    @Override
    public DictData create(DictData dictData) {
        if (dictData.getSortOrder() == null) dictData.setSortOrder(0);
        if (dictData.getStatus() == null) dictData.setStatus(1);
        dictDataMapper.insert(dictData);
        log.info("创建字典项: type={}, code={}", dictData.getDictType(), dictData.getDictCode());
        return dictData;
    }

    @Override
    public DictData update(Long id, DictData dictData) {
        DictData existing = dictDataMapper.selectById(id);
        if (existing == null) throw new BusinessException(404, "字典项不存在");
        dictData.setId(id);
        dictDataMapper.updateById(dictData);
        return dictDataMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        DictData existing = dictDataMapper.selectById(id);
        if (existing == null) throw new BusinessException(404, "字典项不存在");
        dictDataMapper.deleteById(id);
        log.info("删除字典项: id={}", id);
    }
}

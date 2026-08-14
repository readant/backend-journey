package com.readant.cms.service;

import com.readant.cms.entity.DictData;
import java.util.List;

public interface DictDataService {

    List<DictData> getByType(String dictType);

    DictData create(DictData dictData);

    DictData update(Long id, DictData dictData);

    void delete(Long id);
}

package com.readant.cms.controller;

import com.readant.cms.common.R;
import com.readant.cms.entity.DictData;
import com.readant.cms.service.DictDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dict-data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    @GetMapping("/type/{dictType}")
    public R<List<DictData>> getByType(@PathVariable String dictType) {
        return R.success(dictDataService.getByType(dictType));
    }

    @PostMapping
    public R<DictData> create(@RequestBody DictData dictData) {
        return R.success(dictDataService.create(dictData));
    }

    @PutMapping("/{id}")
    public R<DictData> update(@PathVariable Long id, @RequestBody DictData dictData) {
        return R.success(dictDataService.update(id, dictData));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dictDataService.delete(id);
        return R.success();
    }
}

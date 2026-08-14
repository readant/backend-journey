package com.readant.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.R;
import com.readant.cms.entity.OperationLog;
import com.readant.cms.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志管理接口
 */
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping
    public R<Page<OperationLog>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action) {
        return R.success(operationLogService.page(pageNum, pageSize, module, action));
    }
}

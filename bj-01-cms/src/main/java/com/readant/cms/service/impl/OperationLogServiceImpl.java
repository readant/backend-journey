package com.readant.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.entity.OperationLog;
import com.readant.cms.mapper.OperationLogMapper;
import com.readant.cms.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public Page<OperationLog> page(int pageNum, int pageSize, String module, String action) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .eq(module != null && !module.isBlank(), OperationLog::getModule, module)
                .eq(action != null && !action.isBlank(), OperationLog::getAction, action)
                .orderByDesc(OperationLog::getCreatedAt);

        return operationLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
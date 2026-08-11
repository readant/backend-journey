package com.readant.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.entity.OperationLog;

/**
 * 操作日志 Service 接口
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志
     */
    Page<OperationLog> page(int pageNum, int pageSize, String module, String action);
}
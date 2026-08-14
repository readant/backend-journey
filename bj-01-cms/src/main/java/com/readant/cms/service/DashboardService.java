package com.readant.cms.service;

import java.util.Map;

/**
 * 数据看板统计 Service
 */
public interface DashboardService {

    /**
     * 统计看板数据：基础计数、文章状态分布、产品分类分布、近 7 天文章趋势
     */
    Map<String, Object> stats();
}
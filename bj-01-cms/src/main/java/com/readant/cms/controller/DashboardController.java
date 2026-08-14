package com.readant.cms.controller;

import com.readant.cms.common.R;
import com.readant.cms.service.DashboardService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板统计接口
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 数据看板统计
     */
    @GetMapping
    public R<Map<String, Object>> stats() {
        return R.success(dashboardService.stats());
    }
}

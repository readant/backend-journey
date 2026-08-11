package com.readant.cms.controller;

import com.readant.cms.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统健康检查 —— 第一个接口，验证项目能不能跑
 *
 * @RestController 注解：标记这个类是一个"处理网页请求的类"
 * 它会把方法的返回值直接写入 HTTP 响应体（而不是返回一个页面）
 */
@RestController
public class HealthController {

    /**
     * GET /api/v1/health
     *
     * 就像医院的体检科，前端调用这个接口就知道后端服务是否活着。
     * 部署上线后，运维监控系统也会定期调用这个接口检查服务状态。
     */
    @GetMapping("/api/v1/health")
    public R<String> health() {
        return R.success("兴华小组官网服务运行正常");
    }
}
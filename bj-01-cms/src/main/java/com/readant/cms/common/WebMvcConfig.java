package com.readant.cms.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * 1. 将上传目录映射为静态资源路径，使上传的文件可通过 URL 直接访问
 * 2. 注册 Token 鉴权拦截器，保护后台接口（前台接口与健康检查放行）
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地文件系统的 uploads/ 目录
        registry.addResourceHandler(urlPrefix + "/**").addResourceLocations("file:" + uploadPath + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/v1/**")
                // 健康检查、登录入口、前台展示接口无需鉴权
                .excludePathPatterns("/api/v1/health", "/api/v1/admins/login", "/api/v1/site/**");
    }
}

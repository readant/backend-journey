package com.readant.cms.common;

import java.nio.file.Paths;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * <p>1. 将上传目录映射为静态资源路径，使上传的文件可通过 URL 直接访问
 * <p>2. 注册 Token 鉴权拦截器，保护后台接口（前台接口与健康检查放行）
 * <p>3. 配置跨域，供前后端分离开发与部署时调用
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    /** 允许的前端来源，逗号分隔；留空则不注册跨域规则（同源部署时不需要） */
    @Value("${app.cors.allowed-origins:http://localhost:3001,http://localhost:3002}")
    private String allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地文件系统的 uploads/ 目录。
        // 必须用绝对 URI：写成 "file:uploads/" 时，Spring 会把它当相对路径，
        // 最终按 Tomcat 的工作目录解析，与实际落盘位置对不上。
        String location =
                Paths.get(uploadPath).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler(urlPrefix + "/**").addResourceLocations(location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/v1/**")
                // 健康检查、登录入口、前台展示接口无需鉴权
                .excludePathPatterns(
                        "/api/v1/health",
                        "/api/v1/admins/login",
                        "/api/v1/site/**",
                        // 作品分类树是前台筛选用到的只读数据，与 /site/** 同属公开信息。
                        // 前台门户直接调用该路径，若不放行会拿到 401 并在页面上弹出错误。
                        "/api/v1/product-categories/tree");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        // 没配来源就不开跨域：宁可让浏览器报错，也不要默认放行任意站点
        if (origins.length == 0) {
            return;
        }

        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}

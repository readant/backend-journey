package com.readant.cms.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token 鉴权拦截器 —— 后台接口的"门卫"
 *
 * <p>通俗理解：登录时发的 token 是手环，这个拦截器守在 /api/v1/** 门口，
 * 没带手环或手环过期的请求一律挡回去。只保护后台接口，前台展示接口（/site/**）放行。
 *
 * <p>注意：拦截器执行时尚未进入 Controller，@RestControllerAdvice 捕获不到这里的异常，
 * 因此鉴权失败必须自己写 JSON 响应体。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    /** Header 格式：Authorization: Bearer &lt;token&gt; */
    private static final String BEARER_PREFIX = "Bearer ";

    /** 鉴权通过后，把管理员 ID 放进 request，供后续组件（如操作日志切面）取用 */
    public static final String ATTR_ADMIN_ID = "adminId";

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // 1. 跨域预检请求不带 token，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 解析 Authorization: Bearer <token>
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            token = header.substring(BEARER_PREFIX.length()).trim();
        }

        // 3. 校验失败：直接写 401，与 R.error(401, ...) 同构
        //    一次查询同时拿到"是否有效"和"是谁"，避免 validate + getAdminId 两次查表，
        //    也避免两者之间 token 恰好过期导致 adminId 为 null 的竞态。
        Long adminId = (token == null || token.isEmpty()) ? null : tokenService.getAdminId(token);
        if (adminId == null) {
            log.warn("鉴权失败: uri={}, header存在={}", request.getRequestURI(), header != null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\",\"data\":null}");
            return false;
        }

        // 4. 校验通过：放行，并把 adminId 传给下游
        request.setAttribute(ATTR_ADMIN_ID, adminId);
        return true;
    }
}

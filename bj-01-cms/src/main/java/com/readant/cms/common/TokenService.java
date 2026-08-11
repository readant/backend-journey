package com.readant.cms.common;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 服务 —— 登录成功后发放"通行证"
 *
 * 通俗理解：就像游乐园的手环——登录时发一个，后续带着它就能玩所有项目。
 * 当前使用内存存储（重启后失效），后续可升级为 Redis 或 JWT。
 */
@Slf4j
@Component
public class TokenService {

    /** token → adminId 的映射表（ConcurrentHashMap 是线程安全的 HashMap） */
    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    /**
     * 生成 Token 并存储
     */
    public String createToken(Long adminId) {
        // 生成 32 字节随机数，Base64 编码作为 token
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        tokenStore.put(token, adminId);
        return token;
    }

    /**
     * 根据 Token 获取管理员 ID
     */
    public Long getAdminId(String token) {
        return tokenStore.get(token);
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        return tokenStore.containsKey(token);
    }

    /**
     * 移除 Token（退出登录）
     */
    public void removeToken(String token) {
        tokenStore.remove(token);
    }

    @PostConstruct
    public void init() {
        log.info("TokenService 初始化完成（内存存储模式）");
    }
}
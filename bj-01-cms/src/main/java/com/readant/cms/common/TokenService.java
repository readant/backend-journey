package com.readant.cms.common;

import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Token 服务 —— 登录成功后发放"通行证"
 *
 * <p>通俗理解：就像游乐园的手环——登录时发一个，后续带着它就能玩所有项目。
 * 手环有有效期，到点自动失效，不能一直用下去。
 *
 * <p>有效期由 {@code app.auth.token-ttl-seconds} 控制，默认 2 小时。过期条目不会自己消失，
 * 因此这里有两道清理：发新 token 时顺手扫一遍，以及每 10 分钟定时清扫。
 *
 * <p>当前仍是单机内存存储（重启即全部失效），适用于单实例部署；
 * 多实例或需要"重启不掉线"时，应换 Redis 或 JWT。
 */
@Slf4j
@Component
public class TokenService {

    /** SecureRandom 线程安全，做成静态常量即可，不必每次 new */
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final long CLEAN_INTERVAL_MILLIS = 10 * 60 * 1000L;

    /** token → 会话（管理员 ID + 过期时间戳） */
    private final Map<String, Session> tokenStore = new ConcurrentHashMap<>();

    /** 有效期（毫秒） */
    private final long ttlMillis;

    public TokenService(@Value("${app.auth.token-ttl-seconds:7200}") long ttlSeconds) {
        this.ttlMillis = Math.max(ttlSeconds, 0L) * 1000L;
    }

    /**
     * 生成 Token 并存储（同时清理已过期的条目）
     */
    public String createToken(Long adminId) {
        // 生成 32 字节随机数，Base64 编码作为 token
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        purgeExpired();
        tokenStore.put(token, new Session(adminId, System.currentTimeMillis() + ttlMillis));
        return token;
    }

    /**
     * 根据 Token 获取管理员 ID（不存在或已过期返回 null）
     */
    public Long getAdminId(String token) {
        Session session = tokenStore.get(token);
        if (session == null) {
            return null;
        }
        if (session.isExpired()) {
            tokenStore.remove(token);
            return null;
        }
        return session.adminId();
    }

    /**
     * 验证 Token 是否有效（存在且未过期）
     */
    public boolean validateToken(String token) {
        return getAdminId(token) != null;
    }

    /**
     * 移除 Token（退出登录）
     */
    public void removeToken(String token) {
        tokenStore.remove(token);
    }

    /**
     * 有效期秒数，供登录接口回传给前端做会话提示
     */
    public long getTtlSeconds() {
        return ttlMillis / 1000;
    }

    /**
     * 清掉所有已过期的条目，避免 Map 无限增长
     */
    @Scheduled(fixedDelay = CLEAN_INTERVAL_MILLIS)
    public void purgeExpired() {
        int before = tokenStore.size();
        tokenStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int removed = before - tokenStore.size();
        if (removed > 0) {
            log.debug("清理过期 Token {} 个，剩余 {} 个", removed, tokenStore.size());
        }
    }

    @PostConstruct
    public void init() {
        log.info("TokenService 初始化完成（内存存储，有效期 {} 秒）", getTtlSeconds());
    }

    /** 一条会话记录：谁的手环、什么时候到期 */
    private record Session(Long adminId, long expiresAt) {

        boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }
    }
}

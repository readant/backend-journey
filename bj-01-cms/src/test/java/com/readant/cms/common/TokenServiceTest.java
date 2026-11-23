package com.readant.cms.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TokenService 单元测试")
class TokenServiceTest {

    /** 默认有效期 2 小时 */
    private static final long TTL_SECONDS = 7200L;

    private final TokenService tokenService = new TokenService(TTL_SECONDS);

    @Nested
    @DisplayName("createToken 方法")
    class CreateToken {

        @Test
        @DisplayName("生成 Token 并关联管理员ID")
        void shouldCreateAndMap() {
            String token = tokenService.createToken(42L);

            assertThat(token).isNotBlank();
            assertThat(tokenService.validateToken(token)).isTrue();
            assertThat(tokenService.getAdminId(token)).isEqualTo(42L);
        }

        @Test
        @DisplayName("每次生成不同 Token（随机性）")
        void shouldGenerateUniqueTokens() {
            String token1 = tokenService.createToken(1L);
            String token2 = tokenService.createToken(1L);

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("validateToken 方法")
    class ValidateToken {

        @Test
        @DisplayName("不存在的 Token → 无效")
        void shouldBeInvalid_whenUnknown() {
            assertThat(tokenService.validateToken("no-such-token")).isFalse();
        }
    }

    @Nested
    @DisplayName("removeToken 方法")
    class RemoveToken {

        @Test
        @DisplayName("移除后 Token 失效")
        void shouldInvalidateAfterRemove() {
            String token = tokenService.createToken(7L);

            tokenService.removeToken(token);

            assertThat(tokenService.validateToken(token)).isFalse();
            assertThat(tokenService.getAdminId(token)).isNull();
        }
    }

    @Nested
    @DisplayName("有效期（TTL）")
    class Expiry {

        @Test
        @DisplayName("有效期内 → Token 有效，且能读到配置的秒数")
        void shouldBeValidWithinTtl() {
            TokenService service = new TokenService(60);

            String token = service.createToken(1L);

            assertThat(service.validateToken(token)).isTrue();
            assertThat(service.getTtlSeconds()).isEqualTo(60);
        }

        @Test
        @DisplayName("超过有效期 → Token 失效，adminId 取不到")
        void shouldExpireAfterTtl() {
            // TTL=0 表示生成即过期，无需在测试里 sleep
            TokenService service = new TokenService(0);

            String token = service.createToken(1L);

            assertThat(service.validateToken(token)).isFalse();
            assertThat(service.getAdminId(token)).isNull();
        }

        @Test
        @DisplayName("清扫过期条目后再访问，仍然安全返回 null")
        void shouldReturnNullAfterPurge() {
            TokenService service = new TokenService(0);
            String token = service.createToken(1L);

            service.purgeExpired();

            assertThat(service.validateToken(token)).isFalse();
        }
    }
}

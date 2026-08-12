package com.readant.cms.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenService 单元测试")
class TokenServiceTest {

    private final TokenService tokenService = new TokenService();

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
}
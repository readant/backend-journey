package com.readant.cms.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@DisplayName("AuthInterceptor 单元测试")
@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    @Nested
    @DisplayName("preHandle 方法")
    class PreHandle {

        @Test
        @DisplayName("无 Authorization 头 → 返回 401 拦截")
        void shouldReject_whenHeaderMissing() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/articles");
            MockHttpServletResponse response = new MockHttpServletResponse();

            boolean passed = authInterceptor.preHandle(request, response, new Object());

            assertThat(passed).isFalse();
            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(response.getContentAsString()).contains("未登录或登录已过期");
            verify(tokenService, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
        }

        @Test
        @DisplayName("无效 token → 返回 401 拦截")
        void shouldReject_whenTokenInvalid() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/articles");
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad-token");
            MockHttpServletResponse response = new MockHttpServletResponse();
            when(tokenService.validateToken("bad-token")).thenReturn(false);

            boolean passed = authInterceptor.preHandle(request, response, new Object());

            assertThat(passed).isFalse();
            assertThat(response.getStatus()).isEqualTo(401);
            verify(tokenService).validateToken("bad-token");
        }

        @Test
        @DisplayName("有效 token → 放行并写入 adminId")
        void shouldPass_whenTokenValid() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/articles");
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer good-token");
            MockHttpServletResponse response = new MockHttpServletResponse();
            when(tokenService.validateToken("good-token")).thenReturn(true);
            when(tokenService.getAdminId("good-token")).thenReturn(42L);

            boolean passed = authInterceptor.preHandle(request, response, new Object());

            assertThat(passed).isTrue();
            assertThat(request.getAttribute(AuthInterceptor.ATTR_ADMIN_ID)).isEqualTo(42L);
            verify(tokenService).validateToken("good-token");
        }

        @Test
        @DisplayName("OPTIONS 预检 → 无 token 也放行")
        void shouldPass_whenPreflight() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/articles");
            MockHttpServletResponse response = new MockHttpServletResponse();

            boolean passed = authInterceptor.preHandle(request, response, new Object());

            assertThat(passed).isTrue();
            verify(tokenService, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
        }
    }
}

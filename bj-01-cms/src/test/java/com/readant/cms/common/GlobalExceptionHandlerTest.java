package com.readant.cms.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@DisplayName("GlobalExceptionHandler 单元测试")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("handleBusinessException")
    class BusinessExceptionCase {

        @Test
        @DisplayName("业务异常 → 返回对应错误码和消息")
        void shouldMapBusinessException() {
            BusinessException e = new BusinessException(401, "用户名或密码错误");

            R<Void> result = handler.handleBusinessException(e);

            assertThat(result.getCode()).isEqualTo(401);
            assertThat(result.getMsg()).isEqualTo("用户名或密码错误");
            assertThat(result.getData()).isNull();
        }

        @Test
        @DisplayName("默认错误码业务异常 → 返回 500")
        void shouldMapDefaultCode() {
            BusinessException e = new BusinessException("默认业务失败");

            R<Void> result = handler.handleBusinessException(e);

            assertThat(result.getCode()).isEqualTo(500);
            assertThat(result.getMsg()).isEqualTo("默认业务失败");
        }
    }

    @Nested
    @DisplayName("handleValidationException")
    class ValidationExceptionCase {

        @Test
        @DisplayName("参数校验失败 → 返回 400 和第一个错误信息")
        void shouldReturnFirstFieldError() {
            FieldError fieldError = mock(FieldError.class);
            when(fieldError.getDefaultMessage()).thenReturn("标题不能为空");
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldError()).thenReturn(fieldError);
            MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, bindingResult);

            R<Void> result = handler.handleValidationException(e);

            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).isEqualTo("标题不能为空");
        }

        @Test
        @DisplayName("无字段错误 → 返回默认提示")
        void shouldReturnDefaultMsg_whenNoFieldError() {
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldError()).thenReturn(null);
            MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, bindingResult);

            R<Void> result = handler.handleValidationException(e);

            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).isEqualTo("参数校验失败");
        }
    }

    @Nested
    @DisplayName("handleUnknownException")
    class UnknownExceptionCase {

        @Test
        @DisplayName("系统异常 → 返回 500 和兜底提示")
        void shouldReturnFallbackError() {
            R<Void> result = handler.handleUnknownException(new RuntimeException("boom"));

            assertThat(result.getCode()).isEqualTo(500);
            assertThat(result.getMsg()).isEqualTo("服务器内部错误，请稍后重试");
        }
    }
}

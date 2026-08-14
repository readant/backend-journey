package com.readant.cms.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("R 统一返回体单元测试")
class RTest {

    @Nested
    @DisplayName("success 系列")
    class Success {

        @Test
        @DisplayName("无参 success → 200 + success + 无数据")
        void shouldReturnDefault_whenNoArgs() {
            R<Void> result = R.success();

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getMsg()).isEqualTo("success");
            assertThat(result.getData()).isNull();
        }

        @Test
        @DisplayName("带数据 success → 数据透传")
        void shouldCarryData() {
            String data = "hello";

            R<String> result = R.success(data);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isEqualTo("hello");
        }

        @Test
        @DisplayName("自定义提示 success → msg 生效且数据透传")
        void shouldUseCustomMsg() {
            R<String> result = R.success("操作成功", "data");

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getMsg()).isEqualTo("操作成功");
            assertThat(result.getData()).isEqualTo("data");
        }
    }

    @Nested
    @DisplayName("error 系列")
    class Error {

        @Test
        @DisplayName("默认 error → 500 + 提示信息")
        void shouldReturnDefaultError() {
            R<Void> result = R.error("业务失败");

            assertThat(result.getCode()).isEqualTo(500);
            assertThat(result.getMsg()).isEqualTo("业务失败");
            assertThat(result.getData()).isNull();
        }

        @Test
        @DisplayName("自定义错误码 error → 错误码生效")
        void shouldUseCustomCode() {
            R<Void> result = R.error(400, "参数错误");

            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).isEqualTo("参数错误");
        }
    }
}

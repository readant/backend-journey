package com.readant.cms.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.readant.cms.common.R;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 文件上传接口测试
 *
 * <p>重点验证"能不能传"这条边界：上传目录是公开静态资源，
 * 放进来一个 html 就等于允许别人往站点里注入脚本。
 */
@DisplayName("FileController 单元测试")
class FileControllerTest {

    @TempDir
    Path tempDir;

    private FileController controller;

    @BeforeEach
    void setUp() {
        controller = new FileController();
        ReflectionTestUtils.setField(controller, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(controller, "urlPrefix", "/uploads");
        ReflectionTestUtils.setField(controller, "allowedExtensionsRaw", "jpg, png, pdf");
        controller.initAllowedExtensions();
    }

    private long savedFileCount() throws IOException {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.count();
        }
    }

    @Nested
    @DisplayName("允许的类型")
    class Allowed {

        @Test
        @DisplayName("jpg → 保存成功并返回可访问 URL")
        void shouldAcceptJpg() throws IOException {
            MockMultipartFile file = new MockMultipartFile("file", "cover.jpg", "image/jpeg", new byte[] {1, 2, 3});

            R<String> result = controller.upload(file);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).startsWith("/uploads/").endsWith(".jpg");
            assertThat(savedFileCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("扩展名大写 → 同样通过（大小写无关）")
        void shouldAcceptUpperCaseExtension() throws IOException {
            MockMultipartFile file = new MockMultipartFile("file", "COVER.PNG", "image/png", new byte[] {9});

            R<String> result = controller.upload(file);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).endsWith(".png");
            assertThat(savedFileCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("配置文件里的多余空格被忽略")
        void shouldTrimConfiguredExtensions() throws IOException {
            ReflectionTestUtils.setField(controller, "allowedExtensionsRaw", " jpg , pdf ");
            controller.initAllowedExtensions();

            R<String> result =
                    controller.upload(new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[] {1}));

            assertThat(result.getCode()).isEqualTo(200);
        }
    }

    @Nested
    @DisplayName("拒绝的类型")
    class Rejected {

        @Test
        @DisplayName("html → 400，且不落盘")
        void shouldRejectHtml() throws IOException {
            MockMultipartFile file =
                    new MockMultipartFile("file", "evil.html", "text/html", "<script>alert(1)</script>".getBytes());

            R<String> result = controller.upload(file);

            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).contains("不支持的文件类型");
            assertThat(savedFileCount()).isZero();
        }

        @Test
        @DisplayName("svg → 400（可内嵌脚本）")
        void shouldRejectSvg() throws IOException {
            MockMultipartFile file =
                    new MockMultipartFile("file", "logo.svg", "image/svg+xml", "<svg onload=\"alert(1)\"/>".getBytes());

            R<String> result = controller.upload(file);

            assertThat(result.getCode()).isEqualTo(400);
            assertThat(savedFileCount()).isZero();
        }

        @Test
        @DisplayName("没有扩展名 → 400")
        void shouldRejectWithoutExtension() {
            MockMultipartFile file = new MockMultipartFile("file", "README", "text/plain", new byte[] {1});

            R<String> result = controller.upload(file);

            assertThat(result.getCode()).isEqualTo(400);
        }

        @Test
        @DisplayName("空文件 → 400")
        void shouldRejectEmptyFile() {
            MockMultipartFile file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

            R<String> result = controller.upload(file);

            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).isEqualTo("文件不能为空");
        }
    }
}

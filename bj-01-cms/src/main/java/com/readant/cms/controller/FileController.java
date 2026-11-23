package com.readant.cms.controller;

import com.readant.cms.common.R;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 *
 * <p>安全要点：上传目录被映射成了可公开访问的静态资源（见 {@code WebMvcConfig}），
 * 因此"允许上传什么"等于"允许别人在你的域名下展示什么"。若不加白名单，
 * 一个 {@code .html} 或 {@code .svg} 就能变成同源页面，里面写什么脚本都会被执行（存储型 XSS）。
 * 所以这里只放行图片和 PDF，其它一律拒绝。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    /** 允许的扩展名，逗号分隔（不含点） */
    @Value("${app.upload.allowed-extensions:jpg,jpeg,png,gif,webp,pdf}")
    private String allowedExtensionsRaw;

    /** 解析后的白名单，全部小写 */
    private Set<String> allowedExtensions;

    @PostConstruct
    void initAllowedExtensions() {
        this.allowedExtensions = Arrays.stream(allowedExtensionsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        log.info("文件上传白名单: {}", allowedExtensions);
    }

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error(400, "文件不能为空");
        }

        // 1. 取出扩展名并校验（大小写无关）
        String originalName = file.getOriginalFilename();
        String ext = "";
        int dotIndex = originalName == null ? -1 : originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            ext = originalName.substring(dotIndex);
        }
        String bareExt = ext.isEmpty() ? "" : ext.substring(1).toLowerCase(Locale.ROOT);

        if (!allowedExtensions.contains(bareExt)) {
            log.warn("拒绝上传: 文件={}, 扩展名={}", originalName, ext);
            return R.error(400, "不支持的文件类型，仅允许: " + String.join("、", allowedExtensions));
        }

        try {
            // 2. 生成唯一文件名（原名不可信，只取扩展名）
            String fileName = UUID.randomUUID().toString() + ext.toLowerCase(Locale.ROOT);

            // 3. 确保目录存在（先转绝对路径：MultipartFile.transferTo 收到相对路径时，
            //    会按 Tomcat 的临时工作目录去解析，结果是一个不存在的路径，直接抛 IOException）
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 4. 保存文件（normalize 后再校验，确保落点没有跳出上传目录）
            Path targetPath = uploadDir.resolve(fileName).normalize();
            if (!targetPath.startsWith(uploadDir)) {
                log.warn("拒绝上传: 目标路径越界 target={}", targetPath);
                return R.error(400, "非法文件名");
            }
            file.transferTo(targetPath.toFile());

            // 5. 返回可访问的 URL
            String fileUrl = urlPrefix + "/" + fileName;
            log.info("文件上传成功: {}", fileUrl);

            return R.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error(500, "文件上传失败");
        }
    }
}

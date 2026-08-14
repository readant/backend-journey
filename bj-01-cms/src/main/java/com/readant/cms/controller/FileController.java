package com.readant.cms.controller;

import com.readant.cms.common.R;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error(400, "文件不能为空");
        }

        try {
            // 1. 生成唯一文件名
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;

            // 2. 确保目录存在
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 3. 保存文件
            Path targetPath = uploadDir.resolve(fileName);
            file.transferTo(targetPath.toFile());

            // 4. 返回可访问的 URL
            String fileUrl = urlPrefix + "/" + fileName;
            log.info("文件上传成功: {}", fileUrl);

            return R.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error(500, "文件上传失败");
        }
    }
}

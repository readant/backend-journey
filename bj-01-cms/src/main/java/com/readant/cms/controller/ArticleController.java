package com.readant.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.LogOperation;
import com.readant.cms.common.R;
import com.readant.cms.dto.ArticleCreateReq;
import com.readant.cms.dto.ArticleVO;
import com.readant.cms.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @LogOperation(module = "文章", action = "创建")
    @PostMapping
    public R<ArticleVO> create(@Valid @RequestBody ArticleCreateReq req) {
        return R.success(articleService.create(req));
    }

    @GetMapping("/{id}")
    public R<ArticleVO> getById(@PathVariable Long id) {
        return R.success(articleService.getById(id));
    }

    @GetMapping
    public R<Page<ArticleVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId) {
        return R.success(articleService.page(pageNum, pageSize, status, categoryId));
    }

    @LogOperation(module = "文章", action = "更新")
    @PutMapping("/{id}")
    public R<ArticleVO> update(@PathVariable Long id, @Valid @RequestBody ArticleCreateReq req) {
        return R.success(articleService.update(id, req));
    }

    @LogOperation(module = "文章", action = "删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return R.success();
    }
}

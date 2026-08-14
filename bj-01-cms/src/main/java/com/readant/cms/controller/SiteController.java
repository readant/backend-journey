package com.readant.cms.controller;

import com.readant.cms.common.R;
import com.readant.cms.dto.ArticleVO;
import com.readant.cms.entity.Category;
import com.readant.cms.service.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 前台展示接口（无鉴权）
 */
@RestController
@RequestMapping("/api/v1/site")
@RequiredArgsConstructor
public class SiteController {

    private final CategoryService categoryService;
    private final ArticleService articleService;
    private final ProductService productService;

    @GetMapping("/home")
    public R<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoryService.getTree());
        data.put("latestArticles", articleService.page(1, 6, 1, null).getRecords());
        data.put("latestProducts", productService.page(1, 6, null).getRecords());
        return R.success(data);
    }

    @GetMapping("/categories")
    public R<List<Category>> categories() {
        return R.success(categoryService.getTree());
    }

    @GetMapping("/articles/{id}")
    public R<ArticleVO> articleDetail(@PathVariable Long id) {
        return R.success(articleService.getById(id));
    }

    @GetMapping("/products/{id}")
    public R<?> productDetail(@PathVariable Long id) {
        return R.success(productService.getById(id));
    }

    @GetMapping("/articles")
    public R<?> articleList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId) {
        return R.success(articleService.page(pageNum, pageSize, 1, categoryId));
    }

    @GetMapping("/products")
    public R<?> productList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId) {
        return R.success(productService.page(pageNum, pageSize, categoryId));
    }
}

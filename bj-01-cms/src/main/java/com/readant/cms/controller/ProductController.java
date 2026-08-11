package com.readant.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.R;
import com.readant.cms.entity.Product;
import com.readant.cms.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public R<Product> create(@RequestBody Product product) {
        return R.success(productService.create(product));
    }

    @GetMapping("/{id}")
    public R<Product> getById(@PathVariable Long id) {
        return R.success(productService.getById(id));
    }

    @GetMapping
    public R<Page<Product>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long categoryId) {
        return R.success(productService.page(pageNum, pageSize, categoryId));
    }

    @PutMapping("/{id}")
    public R<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return R.success(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return R.success();
    }
}
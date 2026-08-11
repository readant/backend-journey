package com.readant.cms.controller;

import com.readant.cms.common.R;
import com.readant.cms.entity.ProductCategory;
import com.readant.cms.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping("/tree")
    public R<List<ProductCategory>> getTree() {
        return R.success(productCategoryService.getTree());
    }

    @PostMapping
    public R<ProductCategory> create(@RequestBody ProductCategory category) {
        return R.success(productCategoryService.create(category));
    }

    @PutMapping("/{id}")
    public R<ProductCategory> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        return R.success(productCategoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        productCategoryService.delete(id);
        return R.success();
    }
}
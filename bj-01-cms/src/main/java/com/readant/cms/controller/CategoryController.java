package com.readant.cms.controller;

import com.readant.cms.common.R;
import com.readant.cms.entity.Category;
import com.readant.cms.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 栏目管理接口
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    public R<List<Category>> getTree() {
        return R.success(categoryService.getTree());
    }

    @PostMapping
    public R<Category> create(@RequestBody Category category) {
        return R.success(categoryService.create(category));
    }

    @PutMapping("/{id}")
    public R<Category> update(@PathVariable Long id, @RequestBody Category category) {
        return R.success(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return R.success();
    }
}

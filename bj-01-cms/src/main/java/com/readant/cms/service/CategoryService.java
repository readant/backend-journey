package com.readant.cms.service;

import com.readant.cms.entity.Category;

import java.util.List;

/**
 * 栏目 Service 接口
 */
public interface CategoryService {

    /**
     * 查询栏目树（返回树形结构）
     */
    List<Category> getTree();

    /**
     * 创建栏目
     */
    Category create(Category category);

    /**
     * 更新栏目
     */
    Category update(Long id, Category category);

    /**
     * 删除栏目（如果有子栏目则拒绝删除）
     */
    void delete(Long id);
}
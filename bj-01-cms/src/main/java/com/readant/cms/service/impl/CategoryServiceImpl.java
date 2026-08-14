package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.Category;
import com.readant.cms.mapper.CategoryMapper;
import com.readant.cms.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 栏目 Service 实现
 *
 * 核心逻辑：将平铺的数据库记录组装成树形结构
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getTree() {
        // 1. 查询所有栏目（按 sort_order 排序）
        List<Category> all = categoryMapper.selectList(null);

        // 2. 按 parentId 分组
        Map<Long, List<Category>> grouped =
                all.stream().collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));

        // 3. 为每个节点设置 children
        for (Category category : all) {
            List<Category> children = grouped.get(category.getId());
            if (children != null) {
                category.setChildren(children);
            }
        }

        // 4. 返回顶级节点（parent_id IS NULL）
        return grouped.getOrDefault(0L, new ArrayList<>());
    }

    @Override
    public Category create(Category category) {
        // 如果指定了父栏目，检查父栏目是否存在
        if (category.getParentId() != null) {
            Category parent = categoryMapper.selectById(category.getParentId());
            if (parent == null) {
                throw new BusinessException(400, "父栏目不存在");
            }
        }

        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        categoryMapper.insert(category);
        log.info("创建栏目: name={}", category.getName());
        return category;
    }

    @Override
    public Category update(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "栏目不存在");
        }

        category.setId(id);
        categoryMapper.updateById(category);
        log.info("更新栏目: id={}", id);
        return categoryMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        // 检查是否有子栏目
        List<Category> children = categoryMapper.selectList(null);
        boolean hasChildren = children.stream().anyMatch(c -> id.equals(c.getParentId()));
        if (hasChildren) {
            throw new BusinessException(400, "该栏目下有子栏目，请先删除子栏目");
        }

        categoryMapper.deleteById(id);
        log.info("删除栏目: id={}", id);
    }
}

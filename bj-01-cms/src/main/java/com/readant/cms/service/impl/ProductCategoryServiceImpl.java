package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.ProductCategory;
import com.readant.cms.mapper.ProductCategoryMapper;
import com.readant.cms.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    @Override
    public List<ProductCategory> getTree() {
        List<ProductCategory> all = productCategoryMapper.selectList(null);

        Map<Long, List<ProductCategory>> grouped = all.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParentId() == null ? 0L : c.getParentId()));

        for (ProductCategory category : all) {
            List<ProductCategory> children = grouped.get(category.getId());
            if (children != null) {
                category.setChildren(children);
            }
        }

        return grouped.getOrDefault(0L, new ArrayList<>());
    }

    @Override
    public ProductCategory create(ProductCategory category) {
        if (category.getParentId() != null) {
            ProductCategory parent = productCategoryMapper.selectById(category.getParentId());
            if (parent == null) {
                throw new BusinessException(400, "父分类不存在");
            }
        }
        if (category.getSortOrder() == null) category.setSortOrder(0);
        if (category.getStatus() == null) category.setStatus(1);

        productCategoryMapper.insert(category);
        log.info("创建产品分类: name={}", category.getName());
        return category;
    }

    @Override
    public ProductCategory update(Long id, ProductCategory category) {
        ProductCategory existing = productCategoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "分类不存在");
        }
        category.setId(id);
        productCategoryMapper.updateById(category);
        return productCategoryMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        List<ProductCategory> all = productCategoryMapper.selectList(null);
        boolean hasChildren = all.stream().anyMatch(c -> id.equals(c.getParentId()));
        if (hasChildren) {
            throw new BusinessException(400, "该分类下有子分类，请先删除子分类");
        }
        productCategoryMapper.deleteById(id);
        log.info("删除产品分类: id={}", id);
    }
}
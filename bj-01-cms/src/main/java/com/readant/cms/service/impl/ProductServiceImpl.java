package com.readant.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.Product;
import com.readant.cms.mapper.ProductMapper;
import com.readant.cms.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Product create(Product product) {
        if (product.getStatus() == null) product.setStatus(0);
        productMapper.insert(product);
        log.info("创建产品: name={}", product.getName());
        return product;
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw new BusinessException(404, "产品不存在");
        return product;
    }

    @Override
    public Page<Product> page(int pageNum, int pageSize, Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .orderByDesc(Product::getCreatedAt);
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = productMapper.selectById(id);
        if (existing == null) throw new BusinessException(404, "产品不存在");
        product.setId(id);
        productMapper.updateById(product);
        return productMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) throw new BusinessException(404, "产品不存在");
        productMapper.deleteById(id);
        log.info("删除产品: id={}", id);
    }
}

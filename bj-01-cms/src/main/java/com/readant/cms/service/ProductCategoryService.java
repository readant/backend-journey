package com.readant.cms.service;

import com.readant.cms.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryService {

    List<ProductCategory> getTree();

    ProductCategory create(ProductCategory category);

    ProductCategory update(Long id, ProductCategory category);

    void delete(Long id);
}
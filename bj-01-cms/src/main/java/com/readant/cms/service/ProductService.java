package com.readant.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.readant.cms.entity.Product;

public interface ProductService {

    Product create(Product product);

    Product getById(Long id);

    Page<Product> page(int pageNum, int pageSize, Long categoryId);

    Product update(Long id, Product product);

    void delete(Long id);
}

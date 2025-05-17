package com.veloProWeb.service.product.interfaces;

import com.veloProWeb.model.entity.product.CategoryProduct;

import java.util.List;

public interface ICategoryService {
    void save(CategoryProduct category);
    List<CategoryProduct> getAll();
}

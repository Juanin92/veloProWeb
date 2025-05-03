package com.veloProWeb.service.Product.Interfaces;

import com.veloProWeb.model.entity.Product.CategoryProduct;

import java.util.List;

public interface ICategoryService {
    void save(CategoryProduct category);
    List<CategoryProduct> getAll();
}

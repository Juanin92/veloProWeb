package com.veloproweb.service.product.interfaces;

import com.veloproweb.model.entity.product.CategoryProduct;

import java.util.List;

public interface ICategoryService {
    void save(CategoryProduct category);
    List<CategoryProduct> getAll();
}

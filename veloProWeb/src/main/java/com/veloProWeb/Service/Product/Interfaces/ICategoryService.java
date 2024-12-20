package com.veloProWeb.Service.Product.Interfaces;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;

import java.util.List;

public interface ICategoryService {
    void save(CategoryProduct category);
    List<CategoryProduct> getAll();
}

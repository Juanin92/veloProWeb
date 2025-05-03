package com.veloProWeb.service.Product.Interfaces;

import com.veloProWeb.model.entity.Product.SubcategoryProduct;

import java.util.List;

public interface ISubcategoryService {
    void save(SubcategoryProduct subcategory);
    List<SubcategoryProduct> getAll();
    List<SubcategoryProduct> getSubcategoryByCategoryID(Long id);
}

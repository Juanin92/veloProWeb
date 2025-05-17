package com.veloProWeb.service.product.interfaces;

import com.veloProWeb.model.entity.product.SubcategoryProduct;

import java.util.List;

public interface ISubcategoryService {
    void save(SubcategoryProduct subcategory);
    List<SubcategoryProduct> getSubcategoryByCategoryID(Long id);
}

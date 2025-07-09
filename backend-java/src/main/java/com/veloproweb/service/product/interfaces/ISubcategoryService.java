package com.veloproweb.service.product.interfaces;

import com.veloproweb.model.entity.product.SubcategoryProduct;

import java.util.List;

public interface ISubcategoryService {
    void save(SubcategoryProduct subcategory);
    List<SubcategoryProduct> getSubcategoryByCategoryID(Long id);
}

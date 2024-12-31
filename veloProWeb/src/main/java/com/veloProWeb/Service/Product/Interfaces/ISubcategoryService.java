package com.veloProWeb.Service.Product.Interfaces;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;

import java.util.List;

public interface ISubcategoryService {
    void save(SubcategoryProduct subcategory);
    List<SubcategoryProduct> getAll();
    List<SubcategoryProduct> getSubcategoryByCategoryID(Long id);
}

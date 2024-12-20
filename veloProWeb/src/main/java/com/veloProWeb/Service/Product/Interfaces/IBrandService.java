package com.veloProWeb.Service.Product.Interfaces;

import com.veloProWeb.Model.Entity.Product.BrandProduct;

import java.util.List;

public interface IBrandService {
    void save(BrandProduct brand);
    List<BrandProduct> getAll();
}

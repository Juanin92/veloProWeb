package com.veloProWeb.service.Product.Interfaces;

import com.veloProWeb.model.entity.Product.BrandProduct;

import java.util.List;

public interface IBrandService {
    void save(BrandProduct brand);
    List<BrandProduct> getAll();
}

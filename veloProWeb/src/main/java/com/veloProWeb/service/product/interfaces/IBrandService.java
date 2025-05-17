package com.veloProWeb.service.product.interfaces;

import com.veloProWeb.model.entity.product.BrandProduct;

import java.util.List;

public interface IBrandService {
    void save(BrandProduct brand);
    List<BrandProduct> getAll();
}

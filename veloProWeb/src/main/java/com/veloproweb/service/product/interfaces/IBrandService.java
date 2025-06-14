package com.veloproweb.service.product.interfaces;

import com.veloproweb.model.entity.product.BrandProduct;

import java.util.List;

public interface IBrandService {
    void save(BrandProduct brand);
    List<BrandProduct> getAll();
}

package com.veloProWeb.service.Product.Interfaces;

import com.veloProWeb.model.entity.Product.UnitProduct;

import java.util.List;

public interface IUnitService {
    void save(UnitProduct unit);
    List<UnitProduct> getAll();
}

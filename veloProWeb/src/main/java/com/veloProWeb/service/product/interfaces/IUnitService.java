package com.veloProWeb.service.product.interfaces;

import com.veloProWeb.model.entity.product.UnitProduct;

import java.util.List;

public interface IUnitService {
    void save(UnitProduct unit);
    List<UnitProduct> getAll();
}

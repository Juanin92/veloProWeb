package com.veloproweb.service.product.interfaces;

import com.veloproweb.model.entity.product.UnitProduct;

import java.util.List;

public interface IUnitService {
    void save(UnitProduct unit);
    List<UnitProduct> getAll();
}

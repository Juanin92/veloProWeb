package com.veloProWeb.Service.Product.Interfaces;

import com.veloProWeb.Model.Entity.Product.UnitProduct;

import java.util.List;

public interface IUnitService {
    void save(UnitProduct unit);
    List<UnitProduct> getAll();
}

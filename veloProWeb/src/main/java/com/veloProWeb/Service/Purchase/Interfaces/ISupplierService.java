package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.Entity.Purchase.Supplier;

import java.util.List;

public interface ISupplierService {

    void createSupplier(Supplier supplier);
    void updateSupplier(Supplier supplier);
    List<Supplier> getAll();
}

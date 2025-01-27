package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.Entity.Purchase.Supplier;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {

    void createSupplier(Supplier supplier);
    void updateSupplier(Supplier supplier);
    List<Supplier> getAll();
    Optional<Supplier> getSupplierById(Long id);
}

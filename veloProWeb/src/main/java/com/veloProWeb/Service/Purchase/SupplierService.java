package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Service.Purchase.Interfaces.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SupplierService implements ISupplierService {

    @Autowired private SupplierRepo supplierRepo;

    @Override
    public void createSupplier(Supplier supplier) {

    }

    @Override
    public List<Supplier> getAll() {
        return List.of();
    }
}

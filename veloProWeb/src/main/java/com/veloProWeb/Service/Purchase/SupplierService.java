package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Service.Purchase.Interfaces.ISupplierService;
import com.veloProWeb.Validation.SupplierValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class SupplierService implements ISupplierService {

    @Autowired private SupplierRepo supplierRepo;
    @Autowired private SupplierValidator validator;

    @Override
    public void createSupplier(Supplier supplier) {
        Supplier supplierDB = getSupplierCreated(supplier.getRut());
        if (supplierDB != null) {
            throw new IllegalArgumentException("Proveedor Existente: Hay un registro de este proveedor.");
        }else {
            if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
                supplier.setEmail("x@x.xxx");
            }
            validator.validate(supplier);
            supplier.setId(null);
            supplier.setEmail(supplier.getEmail());
            supplier.setName(supplier.getName());
            supplier.setRut(supplier.getRut());
            supplier.setPhone(supplier.getPhone());
            supplierRepo.save(supplier);
        }
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    private Supplier getSupplierCreated(String rut){
        Optional<Supplier> supplier = supplierRepo.findByRut(rut);
        return supplier.orElse(null);
    }
}

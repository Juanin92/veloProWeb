package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Service.Purchase.Interfaces.ISupplierService;
import com.veloProWeb.Validation.SupplierValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService implements ISupplierService {

    @Autowired private SupplierRepo supplierRepo;
    @Autowired private SupplierValidator validator;

    /**
     * Crea un nuevo proveedor
     * Se válida que el proveedor no exista un registro de él, si no lanzara excepción
     * Se válida que el email siempre un valor predeterminado si es nulo o está vacío
     * Se válida todos los datos del proveedor sean correctos
     * @param supplier - Objeto que contiene los valores necesarios
     */
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

    /**
     * Actualiza los datos de un proveedor
     * Se válida que el proveedor no sea nulo, si no lanzara una excepción
     * Se válida que el email siempre un valor predeterminado si es nulo o está vacío
     * Se válida todos los datos del proveedor sean correctos
     * @param supplier - Objeto que contiene los valores necesarios
     */
    @Override
    public void updateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor para actualizar sus datos");
        } else{
            if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
                supplier.setEmail("x@x.xxx");
            }
            validator.validate(supplier);
            supplier.setId(supplier.getId());
            supplier.setEmail(supplier.getEmail());
            supplier.setName(supplier.getName());
            supplier.setRut(supplier.getRut());
            supplier.setPhone(supplier.getPhone());
            supplierRepo.save(supplier);
        }
    }

    /**
     * Obtiene el registro de todos los proveedores
     * @return - Lista con los proveedores encontrados
     */
    @Override
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    /**
     * Busca un proveedor ya creado por su RUT
     * @param rut - rut del proveedor
     * @return - proveedor encontrado o null si no encuentra similitud
     */
    private Supplier getSupplierCreated(String rut){
        Optional<Supplier> supplier = supplierRepo.findByRut(rut);
        return supplier.orElse(null);
    }

    /**
     * Obtener un proveedor por un ID
     * @param id - Identificador del proveedor a obtener
     * @return - Un proveedor si está presente o un objeto vació
     */
    @Override
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepo.findById(id);
    }
}

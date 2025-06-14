package com.veloproweb.validation;

import com.veloproweb.exceptions.supplier.SupplierAlreadyExistsException;
import com.veloproweb.exceptions.supplier.SupplierNotFoundException;
import com.veloproweb.model.entity.purchase.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierValidator {

    /**
     * Válida que el proveedor no exista en el sistema
     * @param supplier - proveedor a consultar
     */
    public void validateSupplierDoesNotExist(Supplier supplier){
        if (supplier != null) {
            throw new SupplierAlreadyExistsException("Ya hay un registro de este proveedor en el sistema.");
        }
    }

    /**
     * Válida que el proveedor exista en el sistema
     * @param supplier - Proveedor a consultar
     */
    public void validateSupplierExists(Supplier supplier){
        if (supplier == null) {
            throw new SupplierNotFoundException("No existe registro del proveedor");
        }
    }
}

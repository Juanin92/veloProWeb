package com.veloproweb.validation;

import com.veloproweb.exceptions.supplier.SupplierAlreadyExistsException;
import com.veloproweb.exceptions.supplier.SupplierNotFoundException;
import com.veloproweb.model.entity.purchase.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SupplierValidatorTest {
    @InjectMocks private SupplierValidator validator;

    //Prueba para validar proveedor no exista
    @Test
    void validateSupplierDoesNotExist_valid(){
        Supplier supplier = Supplier.builder().build();

        SupplierAlreadyExistsException e = assertThrows(SupplierAlreadyExistsException.class,
                () -> validator.validateSupplierDoesNotExist(supplier));
        assertEquals("Ya hay un registro de este proveedor en el sistema.", e.getMessage());
    }

    //Prueba para validar proveedor exista
    @Test
    void validateSupplierExists_valid(){
        SupplierNotFoundException e = assertThrows(SupplierNotFoundException.class,
                () -> validator.validateSupplierExists(null));
        assertEquals("No existe registro del proveedor", e.getMessage());
    }
}

package com.veloProWeb.validation;

import com.veloProWeb.exceptions.supplier.SupplierAlreadyExistsException;
import com.veloProWeb.exceptions.supplier.SupplierNotFoundException;
import com.veloProWeb.model.entity.purchase.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SupplierValidatorTest {
    @InjectMocks private SupplierValidator validator;

    //Prueba para validar proveedor no exista
    @Test
    public void validateSupplierDoesNotExist_valid(){
        Supplier supplier = Supplier.builder().build();

        SupplierAlreadyExistsException e = assertThrows(SupplierAlreadyExistsException.class,
                () -> validator.validateSupplierDoesNotExist(supplier));
        assertEquals("Ya hay un registro de este proveedor en el sistema.", e.getMessage());
    }

    //Prueba para validar proveedor exista
    @Test
    public void validateSupplierExists_valid(){
        SupplierNotFoundException e = assertThrows(SupplierNotFoundException.class,
                () -> validator.validateSupplierExists(null));
        assertEquals("No existe registro del proveedor", e.getMessage());
    }
}

package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Validation.SupplierValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {

    @InjectMocks private SupplierService service;
    @Mock private SupplierRepo repo;
    @Mock private SupplierValidator validator;
    private Supplier supplier;

    @BeforeEach
    void setUp(){
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Samsung");
        supplier.setEmail(null);
        supplier.setRut("12123123-7");
        supplier.setPhone("+569 12345678");
    }

    //Prueba para crear un nuevo proveedor
    @Test
    public void createSupplier_valid(){
        when(repo.findByRut(supplier.getRut())).thenReturn(Optional.empty());
        service.createSupplier(supplier);
        verify(validator).validate(supplier);
        verify(repo).save(supplier);
    }
    @Test
    public void createSupplier_ExistingSupplier(){
        when(repo.findByRut(supplier.getRut())).thenReturn(Optional.of(supplier));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.createSupplier(supplier));
        assertEquals("Proveedor Existente: Hay un registro de este proveedor.", exception.getMessage());
        verify(validator, never()).validate(supplier);
        verify(repo, never()).save(supplier);
    }

    //Prueba para actualizar un proveedor
    @Test
    public void updateSupplier_valid(){
        supplier.setEmail("example@gmail.com");
        supplier.setPhone("+569 12345777");
        service.updateSupplier(supplier);

        assertEquals("example@gmail.com", supplier.getEmail());
        assertEquals("+569 12345777", supplier.getPhone());
        verify(validator).validate(supplier);
        verify(repo).save(supplier);
    }
    @Test
    public void updateSupplier_invalidNull(){
        supplier = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> service.updateSupplier(supplier));
        assertEquals("Debe seleccionar un proveedor para actualizar sus datos", exception.getMessage());
        verify(validator, never()).validate(supplier);
        verify(repo, never()).save(supplier);
    }

    //Prueba para obtener todos los proveedores
    @Test
    public void getAll_valid(){
        service.getAll();
        verify(repo).findAll();
    }
}

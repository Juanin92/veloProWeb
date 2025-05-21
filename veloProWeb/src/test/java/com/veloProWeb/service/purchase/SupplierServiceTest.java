package com.veloProWeb.service.purchase;

import com.veloProWeb.exceptions.supplier.SupplierAlreadyExistsException;
import com.veloProWeb.exceptions.supplier.SupplierNotFoundException;
import com.veloProWeb.mapper.SupplierMapper;
import com.veloProWeb.model.dto.purchase.SupplierRequestDTO;
import com.veloProWeb.model.dto.purchase.SupplierResponseDTO;
import com.veloProWeb.model.entity.purchase.Supplier;
import com.veloProWeb.repository.purchase.SupplierRepo;
import com.veloProWeb.validation.SupplierValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {

    @InjectMocks private SupplierService service;
    @Mock private SupplierRepo repo;
    @Mock private SupplierValidator validator;
    @Spy private SupplierMapper mapper;
    private Supplier supplier;

    @BeforeEach
    void setUp(){
        supplier = Supplier.builder().id(1L).rut("12345678-9").name("Sony").email("sony@test.com")
                .phone("+569 12345678").build();
    }

    //Prueba para crear un nuevo proveedor
    @Test
    public void createSupplier_valid(){
        SupplierRequestDTO dto = SupplierRequestDTO.builder().rut("12345678-9").name("Sony").email("sony@test.com")
                .phone("+569 12345678").build();
        when(repo.findByRut(supplier.getRut())).thenReturn(Optional.empty());
        doNothing().when(validator).validateSupplierDoesNotExist(null);

        ArgumentCaptor<Supplier> supplierArgument = ArgumentCaptor.forClass(Supplier.class);
        service.createSupplier(dto);

        verify(repo, times(1)).findByRut(dto.getRut());
        verify(validator, times(1)).validateSupplierDoesNotExist(null);
        verify(mapper, times(1)).toEntity(dto);
        verify(repo, times(1)).save(supplierArgument.capture());

        Supplier result = supplierArgument.getValue();
        assertEquals(result.getRut(), dto.getRut());
        assertEquals(result.getName(), dto.getName());
        assertEquals(result.getPhone(), dto.getPhone());
        assertEquals(result.getEmail(), dto.getEmail());
    }
    @Test
    public void createSupplier_ExistingSupplier(){
        SupplierRequestDTO dto = SupplierRequestDTO.builder().rut("12345678-9").name("Sony").email("sony@test.com")
                .phone("+569 12345678").build();
        when(repo.findByRut(dto.getRut())).thenReturn(Optional.of(supplier));
        doThrow(new SupplierAlreadyExistsException("Ya hay un registro de este proveedor en el sistema."))
                .when(validator).validateSupplierDoesNotExist(supplier);

        SupplierAlreadyExistsException exception = assertThrows(SupplierAlreadyExistsException.class,
                () -> service.createSupplier(dto));
        assertEquals("Ya hay un registro de este proveedor en el sistema.", exception.getMessage());

        verify(validator, times(1)).validateSupplierDoesNotExist(supplier);
        verify(repo, never()).save(supplier);
    }

    //Prueba para actualizar un proveedor
    @Test
    public void updateSupplier_valid(){
        SupplierRequestDTO dto = SupplierRequestDTO.builder().rut("12345678-9").name("Sony").email("sony@test.cl")
                .phone("+569 12345321").build();
        when(repo.findByRut(dto.getRut())).thenReturn(Optional.of(supplier));
        doNothing().when(validator).validateSupplierExists(supplier);

        ArgumentCaptor<Supplier> supplierArgument = ArgumentCaptor.forClass(Supplier.class);
        service.updateSupplier(dto);

        verify(repo, times(1)).findByRut(dto.getRut());
        verify(validator, times(1)).validateSupplierExists(supplier);
        verify(mapper, times(1)).updateSupplierFromDto(supplierArgument.capture(), eq(dto));
        verify(repo, times(1)).save(supplierArgument.capture());

        Supplier result = supplierArgument.getValue();
        assertEquals(result.getEmail(), dto.getEmail());
        assertEquals(result.getPhone(), dto.getPhone());
        assertEquals(result.getName(), dto.getName());
        assertEquals(result.getRut(), dto.getRut());
    }
    @Test
    public void updateSupplier_invalidNull(){
        SupplierRequestDTO dto = SupplierRequestDTO.builder().rut("12345678-9").name("Sony").email("sony@test.cl")
                .phone("+569 12345321").build();
        when(repo.findByRut(dto.getRut())).thenReturn(Optional.empty());
        doThrow(new SupplierNotFoundException("No existe registro del proveedor"))
                .when(validator).validateSupplierExists(null);

        SupplierNotFoundException exception = assertThrows(SupplierNotFoundException.class,
                () -> service.updateSupplier(dto));
        assertEquals("No existe registro del proveedor", exception.getMessage());
        verify(validator, times(1)).validateSupplierExists(null);
        verify(repo, never()).save(supplier);
    }

    //Prueba para obtener todos los proveedores
    @Test
    public void getAll_valid(){
        Supplier supplier1 = Supplier.builder().id(2L).rut("12345632-9").name("Microsoft").email("microsoft@test.com")
                .phone("+569 12345678").build();
        when(repo.findAll()).thenReturn(List.of(supplier, supplier1));

        List<SupplierResponseDTO> result = service.getAll();
        verify(repo, times(1)).findAll();

        assertEquals(result.getFirst().getName(), supplier.getName());
        assertEquals(result.get(1).getName(), supplier1.getName());
    }

    //Prueba para obtener un proveedor por rut
    @Test
    public void getDtoByRut_valid(){
        SupplierResponseDTO response = SupplierResponseDTO.builder().rut("12345678-9").name("Sony")
                .email("sony@test.com").phone("+569 12345678").build();
        when(repo.findByRut(supplier.getRut())).thenReturn(Optional.of(supplier));
        when(mapper.responseDTO(supplier)).thenReturn(response);

        SupplierResponseDTO result = service.getDtoByRut(supplier.getRut());

        verify(repo, times(1)).findByRut(supplier.getRut());

        assertEquals(result.getName(), response.getName());
    }
}

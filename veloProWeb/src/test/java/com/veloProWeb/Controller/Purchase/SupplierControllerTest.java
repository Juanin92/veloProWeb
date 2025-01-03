package com.veloProWeb.Controller.Purchase;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Service.Purchase.Interfaces.ISupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SupplierControllerTest {

    @InjectMocks private SupplierController supplierController;
    @Mock private ISupplierService supplierService;
    @Autowired private MockMvc mockMvc;
    private Supplier supplier;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(supplierController).setControllerAdvice(new GlobalExceptionHandler()).build();
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Samsung");
        supplier.setEmail(null);
        supplier.setRut("12123123-7");
        supplier.setPhone("+569 12345678");
    }

    //Prueba para obtener una lista de proveedores
    @Test
    public void getListSupplier_valid() throws Exception {
        List<Supplier> suppliers = Collections.singletonList(supplier);
        when(supplierService.getAll()).thenReturn(suppliers);

        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].name").value("Samsung"));

        verify(supplierService,times(1)).getAll();
    }
    @Test
    public void getListProducts_error() throws Exception {
        when(supplierService.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(supplierService, times(1)).getAll();
    }
    @Test
    public void getListProductsNull_valid() throws Exception {
        when(supplierService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(supplierService, times(1)).getAll();
    }

    //Prueba para crear un nuevo proveedor
    @Test
    public void createSupplier_valid() throws Exception {
        mockMvc.perform(post("/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"name\": \"Samsung\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Proveedor creado exitosamente!"));

        ArgumentCaptor<Supplier> supplierArgumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(supplierService, times(1)).createSupplier(supplierArgumentCaptor.capture());
        Supplier supplier = supplierArgumentCaptor.getValue();
        assertEquals(1L, supplier.getId());
        assertEquals("Samsung", supplier.getName());
    }
    @Test
    public void createSupplier_invalidExistingSupplier() throws Exception {
        doThrow(new IllegalArgumentException("Proveedor ya existe registro"))
                .when(supplierService).createSupplier(any(Supplier.class));
        mockMvc.perform(post("/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"name\": \"Samsung\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Proveedor ya existe registro"));

        ArgumentCaptor<Supplier> supplierArgumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(supplierService, times(1)).createSupplier(supplierArgumentCaptor.capture());
        Supplier supplier = supplierArgumentCaptor.getValue();
        assertEquals(1L, supplier.getId());
        assertEquals("Samsung", supplier.getName());
    }
}

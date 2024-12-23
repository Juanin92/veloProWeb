package com.veloProWeb.Controller.Product;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Service.Product.Interfaces.IBrandService;
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
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BrandControllerTest {

    @InjectMocks private BrandController controller;
    @Mock private IBrandService service;
    @Autowired private MockMvc mockMvc;
    private BrandProduct brand;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        brand = new BrandProduct();
        brand.setId(1L);
        brand.setName("Samsung");
    }

    //Prueba para obtener lista de marcas registradas
    @Test
    public void getAllBrandsNull_valid() throws Exception {
        when(service.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/marcas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllBrandsData_valid() throws Exception {
        List<BrandProduct> brands = Collections.singletonList(brand);
        when(service.getAll()).thenReturn(brands);

        mockMvc.perform(get("/marcas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].name").value("Samsung"));
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllBrands_error() throws Exception {
        when(service.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/marcas"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(service, times(1)).getAll();
    }

    //Prueba para crear una nueva marca
    @Test
    public void createBrand_valid() throws Exception {
        mockMvc.perform(post("/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"name\": \"Samsung\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Marca registrada correctamente"));

        ArgumentCaptor<BrandProduct> brandArgumentCaptor = ArgumentCaptor.forClass(BrandProduct.class);
        verify(service, times(1)).save(brandArgumentCaptor.capture());
        BrandProduct brandProduct = brandArgumentCaptor.getValue();
        assertEquals(1L, brandProduct.getId());
        assertEquals("Samsung", brandProduct.getName());
    }
    @Test
    public void createBrand_invalidExistingUnit() throws Exception {
        doThrow(new IllegalArgumentException("Marca ya existe registro"))
                .when(service).save(brand);
        mockMvc.perform(post("/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"name\": \"Samsung\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Marca ya existe registro"));

        ArgumentCaptor<BrandProduct> brandArgumentCaptor = ArgumentCaptor.forClass(BrandProduct.class);
        verify(service, times(1)).save(brandArgumentCaptor.capture());
        BrandProduct brandProduct = brandArgumentCaptor.getValue();
        assertEquals(1L, brandProduct.getId());
        assertEquals("Samsung", brandProduct.getName());
    }
}

package com.veloProWeb.Controller.Product;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Service.Product.Interfaces.ISubcategoryService;
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
public class SubcategoryControllerTest {

    @InjectMocks private SubcategoryController controller;
    @Mock private ISubcategoryService service;
    @Autowired private MockMvc mockMvc;
    private SubcategoryProduct subcategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        subcategory = new SubcategoryProduct();
        subcategory.setId(1L);
        subcategory.setName("Tech");
    }

    //Prueba para obtener lista de subcategorías registradas
    @Test
    public void getAllSubcategoriesNull_valid() throws Exception {
        when(service.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/subcategoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllSubcategoriesData_valid() throws Exception {
        List<SubcategoryProduct> subcategories = Collections.singletonList(subcategory);
        when(service.getAll()).thenReturn(subcategories);

        mockMvc.perform(get("/subcategoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].name").value("Tech"));
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllSubcategories_error() throws Exception {
        when(service.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/subcategoria"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(service, times(1)).getAll();
    }

    //Prueba para crear una nueva subcategoría
//    @Test
//    public void createBrand_valid() throws Exception {
//        mockMvc.perform(post("/subcategoria")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"id\": \"1\", \"name\": \"Tech\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value( "Marca registrada correctamente"));
//
//        ArgumentCaptor<SubcategoryProduct> subcategoryArgumentCaptor = ArgumentCaptor.forClass(SubcategoryProduct.class);
//        verify(service, times(1)).save(subcategoryArgumentCaptor.capture());
//        SubcategoryProduct subcategoryProduct = subcategoryArgumentCaptor.getValue();
//        assertEquals(1L, subcategoryProduct.getId());
//        assertEquals("Tech", subcategoryProduct.getName());
//    }
//    @Test
//    public void createBrand_invalidExistingUnit() throws Exception {
//        doThrow(new IllegalArgumentException("Marca ya existe registro"))
//                .when(service).save(subcategory);
//        mockMvc.perform(post("/subcategoria")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"id\": \"1\", \"name\": \"Tech\"}"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Marca ya existe registro"));
//
//        ArgumentCaptor<SubcategoryProduct> subcategoryArgumentCaptor = ArgumentCaptor.forClass(SubcategoryProduct.class);
//        verify(service, times(1)).save(subcategoryArgumentCaptor.capture());
//        SubcategoryProduct subcategoryProduct = subcategoryArgumentCaptor.getValue();
//        assertEquals(1L, subcategoryProduct.getId());
//        assertEquals("Tech", subcategoryProduct.getName());
//    }
}

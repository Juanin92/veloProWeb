package com.veloProWeb.Controller.Product;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.UnitProduct;
import com.veloProWeb.Service.Product.Interfaces.ICategoryService;
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
public class CategoryControllerTest {

    @InjectMocks private CategoryController controller;
    @Mock private ICategoryService service;
    @Autowired private MockMvc mockMvc;
    private CategoryProduct category;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        category = new CategoryProduct();
        category.setId(1L);
        category.setName("Comida");
    }

    //Prueba para obtener lista de categorías registradas
    @Test
    public void getAllCategoriesNull_valid() throws Exception {
        when(service.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllCategoriesData_valid() throws Exception {
        List<CategoryProduct> categories = Collections.singletonList(category);
        when(service.getAll()).thenReturn(categories);

        mockMvc.perform(get("/categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].name").value("Comida"));
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllCategories_error() throws Exception {
        when(service.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/categoria"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(service, times(1)).getAll();
    }

    //Prueba para crear una nueva unidad de medida
    @Test
    public void createCategory_valid() throws Exception {
        mockMvc.perform(post("/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"name\": \"Comida\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Categoría registrada correctamente"));

        ArgumentCaptor<CategoryProduct> categoryArgumentCaptor = ArgumentCaptor.forClass(CategoryProduct.class);
        verify(service, times(1)).save(categoryArgumentCaptor.capture());
        CategoryProduct categoryCategory = categoryArgumentCaptor.getValue();
        assertEquals(1L, categoryCategory.getId());
        assertEquals("Comida", categoryCategory.getName());
    }
    @Test
    public void createCategory_invalidExistingUnit() throws Exception {
        doThrow(new IllegalArgumentException("Categoría ya existe registro"))
                .when(service).save(category);
        mockMvc.perform(post("/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"name\": \"Comida\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value( "Categoría ya existe registro"));

        ArgumentCaptor<CategoryProduct> categoryArgumentCaptor = ArgumentCaptor.forClass(CategoryProduct.class);
        verify(service, times(1)).save(categoryArgumentCaptor.capture());
        CategoryProduct categoryCategory = categoryArgumentCaptor.getValue();
        assertEquals(1L, categoryCategory.getId());
        assertEquals("Comida", categoryCategory.getName());
    }
}

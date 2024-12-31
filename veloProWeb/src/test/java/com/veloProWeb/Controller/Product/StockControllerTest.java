package com.veloProWeb.Controller.Product;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Product.*;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {

    @InjectMocks private StockController stockController;
    @Mock private IProductService productService;
    @Autowired private MockMvc mockMvc;
    private Product product;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).setControllerAdvice(new GlobalExceptionHandler()).build();
        product = new Product();
    }

    //Pruebas para obtener una lista de todos los productos
    @Test
    public void getListProductsNull_valid() throws Exception {
        when(productService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(productService, times(1)).getAll();
    }
    @Test
    public void getListProductsData_valid() throws Exception {
        product = new Product();
        product.setId(1L);

        BrandProduct brand = new BrandProduct();
        brand.setId(1L);
        brand.setName("Marca");
        product.setBrand(brand);

        UnitProduct unit = new UnitProduct();
        unit.setNameUnit("Unidad");
        product.setUnit(unit);

        SubcategoryProduct subcategoryProduct = new SubcategoryProduct();
        subcategoryProduct.setName("Subcategoría");
        product.setSubcategoryProduct(subcategoryProduct);

        CategoryProduct category = new CategoryProduct();
        category.setName("Categoría");
        product.setCategory(category);

        List<Product> products = Collections.singletonList(product);
        when(productService.getAll()).thenReturn(products);

        mockMvc.perform(get("/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].brand.name").value("Marca"))
                .andExpect(jsonPath("[0].unit.nameUnit").value("Unidad"))
                .andExpect(jsonPath("[0].subcategoryProduct.name").value("Subcategoría"))
                .andExpect(jsonPath("[0].category.name").value("Categoría"));

        verify(productService, times(1)).getAll();
    }

    @Test
    public void getListProducts_error() throws Exception {
        when(productService.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/stock"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(productService, times(1)).getAll();
    }

    //Prueba para crear un nuevo producto
    @Test
    public void createBrand_valid() throws Exception {
        mockMvc.perform(post("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"description\": \"Samsung\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Producto creado exitosamente!"));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).create(productArgumentCaptor.capture());
        Product product = productArgumentCaptor.getValue();
        assertEquals(1L, product.getId());
        assertEquals("Samsung", product.getDescription());
    }
    @Test
    public void createBrand_invalidExistingUnit() throws Exception {
        product.setId(1L);
        product.setDescription("Samsung");
        doThrow(new IllegalArgumentException("Producto ya existe registro"))
                .when(productService).create(product);
        mockMvc.perform(post("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"description\": \"Samsung\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Producto ya existe registro"));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).create(productArgumentCaptor.capture());
        Product product = productArgumentCaptor.getValue();
        assertEquals(1L, product.getId());
        assertEquals("Samsung", product.getDescription());
    }
}

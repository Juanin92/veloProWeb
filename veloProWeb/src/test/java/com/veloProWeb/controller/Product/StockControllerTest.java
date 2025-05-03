package com.veloProWeb.controller.Product;

import com.veloProWeb.exceptions.Handlers.GlobalExceptionHandler;
import com.veloProWeb.model.entity.Product.*;
import com.veloProWeb.model.Enum.StatusProduct;
import com.veloProWeb.service.Product.Interfaces.IProductService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        product.setId(1L);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);

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
    public void createProduct_valid() throws Exception {
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
    public void createProduct_invalidExisting() throws Exception {
        product.setDescription("Samsung");
        doThrow(new IllegalArgumentException("Producto ya existe registro"))
                .when(productService).create(any(Product.class));
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

    //Prueba para actualizar un producto
    @Test
    public void updateProduct_validWithStockValue() throws Exception {
        mockMvc.perform(put("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"description\": \"Asus\", \"stock\": \"10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Producto actualizado exitosamente!"));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).update(productArgumentCaptor.capture());
        Product product = productArgumentCaptor.getValue();
        assertEquals(1L, product.getId());
        assertEquals("Asus", product.getDescription());
        assertEquals(10, product.getStock());
    }
    @Test
    public void updateProduct_validNonStockValue() throws Exception {
        mockMvc.perform(put("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"description\": \"Asus\"}, \"stock\": \"0\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Producto actualizado exitosamente!"));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).update(productArgumentCaptor.capture());
        Product product = productArgumentCaptor.getValue();
        assertEquals(1L, product.getId());
        assertEquals("Asus", product.getDescription());
        assertEquals(0, product.getStock());
    }

    //Prueba para eliminar un producto
    @Test
    public void deleteProduct_valid() throws Exception {
        mockMvc.perform(put("/stock/eliminar_producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"description\": \"Asus\", \"status\": \"true\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Producto eliminado exitosamente!"));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).delete(productArgumentCaptor.capture());
        Product product = productArgumentCaptor.getValue();
        assertEquals(1L, product.getId());
    }

    //Prueba para activar un producto
    @Test
    public void activeProduct_valid() throws Exception {
        mockMvc.perform(put("/stock/activar_producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"description\": \"Asus\", \"status\": \"false\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Producto activado exitosamente!"));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).active(productArgumentCaptor.capture());
        Product product = productArgumentCaptor.getValue();
        assertEquals(1L, product.getId());
    }
}

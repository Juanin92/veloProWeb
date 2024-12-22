package com.veloProWeb.Controller.Product;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.DTO.ProductDTO;
import com.veloProWeb.Model.Entity.Product.*;
import com.veloProWeb.Model.Enum.StatusProduct;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {

    @InjectMocks private StockController stockController;
    @Mock private IProductService productService;
    @Autowired private MockMvc mockMvc;
    private Product product;
    private ProductDTO dto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).setControllerAdvice(new GlobalExceptionHandler()).build();
        product = new Product();
        dto = new ProductDTO(1L, "description", 10000, 5000, 10, true, StatusProduct.DISPONIBLE,"Samsung","1 UN", "Mobile", "Tech");
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

        // Configurar campos adicionales temporalmente para depuración
        BrandProduct brand = new BrandProduct();
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
                .andExpect(jsonPath("[0].brand").value("Marca"))
                .andExpect(jsonPath("[0].unit").value("Unidad"))
                .andExpect(jsonPath("[0].subcategoryProduct").value("Subcategoría"))
                .andExpect(jsonPath("[0].category").value("Categoría"));

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
}

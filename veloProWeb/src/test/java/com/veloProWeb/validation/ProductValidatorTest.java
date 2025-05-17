package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.ProductAlreadyActivatedException;
import com.veloProWeb.exceptions.product.ProductAlreadyDeletedException;
import com.veloProWeb.model.entity.Product.*;
import com.veloProWeb.model.Enum.StatusProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductValidatorTest {
    @InjectMocks private ProductValidator validator;
    private Product product;

    @BeforeEach
    void setUp(){
        product = Product.builder()
                .id(1L)
                .description("Description Product")
                .salePrice(1000).buyPrice(2000)
                .stock(15).reserve(0).threshold(3)
                .status(true).statusProduct(StatusProduct.DISPONIBLE)
                .brand(new BrandProduct()).unit(new UnitProduct())
                .subcategoryProduct(new SubcategoryProduct())
                .category(new CategoryProduct()).build();
    }

    //Prueba para validar monto del stock del producto
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void validateStock_invalid(int value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateStock(value));
        assertEquals("Cantidad de ser mayor a 0", exception.getMessage());
    }

    //Prueba para validar monto del umbral crítico del producto
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void validateThreshold_invalid(int value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateThreshold(value));
        assertEquals("Cantidad de ser mayor a 0", exception.getMessage());
    }

    //Prueba para validar monto del precio de venta del producto
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void validateSalePrice_invalid(int value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateSalePrice(value));
        assertEquals("Precio de ser mayor a 0", exception.getMessage());
    }

    //Prueba para validar si el producto está activado
    @Test
    public void isActivated_invalid(){
        ProductAlreadyActivatedException e = assertThrows(ProductAlreadyActivatedException.class,
                ()-> validator.isActivated(product));
        assertEquals("El producto ya está activado.", e.getMessage());
    }

    //Prueba para validar si el producto está desactivado
    @Test
    public void isDeleted_invalid(){
        product.setStatus(false);
        ProductAlreadyDeletedException e = assertThrows(ProductAlreadyDeletedException.class,
                ()-> validator.isDeleted(product));
        assertEquals("El producto ya está desactivado.", e.getMessage());
    }

    //Prueba para validar el cambio de stock
    @Test
    public void isChangeStockOriginalValue_validChange(){
        int newStock = 11;
        boolean result = validator.isChangeStockOriginalValue(product.getStock(), newStock);
        assertTrue(result);
    }
    @Test
    public void isChangeStockOriginalValue_validNotChange(){
        int newStock = 15;
        boolean result = validator.isChangeStockOriginalValue(product.getStock(), newStock);
        assertFalse(result);
    }
}

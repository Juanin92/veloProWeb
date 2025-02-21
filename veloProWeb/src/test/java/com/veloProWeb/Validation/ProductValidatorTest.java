package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Product.*;
import com.veloProWeb.Model.Enum.StatusProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProductValidatorTest {
    @InjectMocks private ProductValidator validator;
    @Mock private BrandProduct brand;
    @Mock private CategoryProduct category;
    @Mock private SubcategoryProduct subcategory;
    @Mock private UnitProduct unit;
    private Product product;

    @BeforeEach
    void setUp(){
        product = new Product(1l, "Descripción", 1000, 2000, 15, 0,3, true, StatusProduct.DISPONIBLE, brand, unit, subcategory, category, new ArrayList<>());
    }

    @Test
    public void validateNewProduct_valid(){
        validator.validateNewProduct(product);
    }

    //Prueba para validar una marca
    @Test
    public void validateBrand_invalid(){
        product.setBrand(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Seleccione una marca", exception.getMessage());
    }

    //Prueba para validar una categoría
    @Test
    public void validateCategory_invalid(){
        product.setCategory(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Seleccione una categoría", exception.getMessage());
    }

    //Prueba para validar una subcategoría
    @Test
    public void validateSubcategory_invalid(){
        product.setSubcategoryProduct(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Seleccione una subcategoría", exception.getMessage());
    }

    //Prueba para validar una unidad de medida
    @Test
    public void validateUnit_invalid(){
        product.setUnit(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Seleccione una unidad", exception.getMessage());
    }

    //Prueba para validar una descripción del producto
    @Test
    public void validateDescription_invalid(){
        product.setDescription(" ");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Ingrese una descripción", exception.getMessage());
    }
    @Test
    public void validateDescription_invalidNull(){
        product.setDescription(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Ingrese una descripción", exception.getMessage());
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
}

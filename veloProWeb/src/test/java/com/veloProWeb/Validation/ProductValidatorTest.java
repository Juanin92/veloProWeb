package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.StatusProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProductValidatorTest {
    @InjectMocks private ProductValidator validator;
    private Product product;

    @BeforeEach
    void setUp(){
        product = new Product(1l, "Descripción", 1000, 2000, 15, true, StatusProduct.AVAILABLE, null, null, null, null, new ArrayList<>());
    }

    //Prueba para validar una marca válida
    @Test
    public void validateBrand_valid(){
        validator.validateNewProduct(product);
    }
    @Test
    public void validateBrand_invalid(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateNewProduct(product));
        assertEquals("Seleccione una marca", exception.getMessage());
    }

    private void validateBrand(BrandProduct brand){
        if (brand == null){
            throw new IllegalArgumentException("Seleccione una marca");
        }
    }
}

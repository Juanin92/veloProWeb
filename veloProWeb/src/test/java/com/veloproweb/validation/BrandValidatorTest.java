package com.veloproweb.validation;

import com.veloproweb.exceptions.product.BrandAlreadyExistsException;
import com.veloproweb.model.entity.product.BrandProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BrandValidatorTest {

    @InjectMocks private BrandValidator validator;

    //Prueba para validar la existencia de una marca
    @Test
    void validateBrand_validException(){
        BrandProduct brand = BrandProduct.builder().build();
        BrandAlreadyExistsException e = assertThrows(BrandAlreadyExistsException.class,
                () -> validator.validateBrandDoesNotExist(brand));
        assertEquals("Nombre Existente: Hay registro de esta marca.", e.getMessage());
    }
}

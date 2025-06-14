package com.veloproweb.validation;

import com.veloproweb.exceptions.product.CategoryAlreadyExistsException;
import com.veloproweb.model.entity.product.CategoryProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CategoryValidatorTest {

    @InjectMocks private CategoryValidator validator;

    //Prueba para validar la existencia de una categoría
    @Test
    void validateCategory_validException(){
        CategoryProduct category = CategoryProduct.builder().build();
        CategoryAlreadyExistsException e = assertThrows(CategoryAlreadyExistsException.class,
                () -> validator.validateCategoryDoesNotExist(category));
        assertEquals("Nombre Existente: Hay registro de esta categoría.", e.getMessage());
    }
}

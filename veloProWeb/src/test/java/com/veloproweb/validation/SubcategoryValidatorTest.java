package com.veloproweb.validation;

import com.veloproweb.exceptions.product.CategoryNotFoundException;
import com.veloproweb.exceptions.product.SubcategoryAlreadyExistsException;
import com.veloproweb.model.entity.product.CategoryProduct;
import com.veloproweb.model.entity.product.SubcategoryProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubcategoryValidatorTest {

    @InjectMocks private SubcategoryValidator validator;

    //Prueba para validar una subcategoría existe
    @Test
    void validateSubcategory_validNotExistException(){
        CategoryProduct category = CategoryProduct.builder().id(1L).name("Food").build();
        SubcategoryProduct subcategory = SubcategoryProduct.builder().id(1L).name("Milk").category(category).build();
        SubcategoryAlreadyExistsException exception = assertThrows(SubcategoryAlreadyExistsException.class,
                () -> validator.validateSubcategoryDoesNotExist(subcategory));
        assertEquals(String.format("Nombre Existente: Hay registro de esta Subcategoría en la Categoría %s",
                subcategory.getCategory().getName()), exception.getMessage());
    }

    //Prueba si la subcategoría tiene una categoría asociada
    @Test
    void validateSubcategory_validHasCategoryException(){
        SubcategoryProduct subcategory = SubcategoryProduct.builder().id(1L).name("Milk").category(null).build();
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> validator.validateSubcategoryHasCategory(subcategory));
        assertEquals("Dede seleccionar una categoría.", exception.getMessage());
    }
}

package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.CategoryNotFoundException;
import com.veloProWeb.exceptions.product.SubcategoryAlreadyExistsException;
import com.veloProWeb.model.entity.product.CategoryProduct;
import com.veloProWeb.model.entity.product.SubcategoryProduct;
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
    public void validateSubcategory_validNotExistException(){
        CategoryProduct category = CategoryProduct.builder().id(1L).name("Food").build();
        SubcategoryProduct subcategory = SubcategoryProduct.builder().id(1L).name("Milk").category(category).build();
        SubcategoryAlreadyExistsException exception = assertThrows(SubcategoryAlreadyExistsException.class,
                () -> validator.validateSubcategoryDoesNotExist(subcategory));
        assertEquals(String.format("Nombre Existente: Hay registro de esta Subcategoría en la Categoría %s",
                subcategory.getCategory().getName()), exception.getMessage());
    }

    //Prueba si la subcategoría tiene una categoría asociada
    @Test
    public void validateSubcategory_validHasCategoryException(){
        SubcategoryProduct subcategory = SubcategoryProduct.builder().id(1L).name("Milk").category(null).build();
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> validator.validateSubcategoryHasCategory(subcategory));
        assertEquals("Dede seleccionar una categoría.", exception.getMessage());
    }
}

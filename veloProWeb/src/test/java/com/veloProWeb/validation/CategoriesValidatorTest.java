package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.BrandAlreadyExistsException;
import com.veloProWeb.exceptions.product.CategoryAlreadyExistsException;
import com.veloProWeb.exceptions.product.UnitAlreadyExistsException;
import com.veloProWeb.exceptions.validation.ValidationException;
import com.veloProWeb.model.entity.Product.BrandProduct;
import com.veloProWeb.model.entity.Product.CategoryProduct;
import com.veloProWeb.model.entity.Product.UnitProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CategoriesValidatorTest {

    @InjectMocks private CategoriesValidator validator;

    //Prueba para validar la existencia de una marca
    @Test
    public void validateBrand_validException(){
        BrandProduct brand = BrandProduct.builder().build();
        BrandAlreadyExistsException e = assertThrows(BrandAlreadyExistsException.class,
                () -> validator.validateBrand(brand));
        assertEquals("Nombre Existente: Hay registro de esta marca.", e.getMessage());
    }

    //Prueba para validar la existencia de una categoría
    @Test
    public void validateCategory_validException(){
        CategoryProduct category = CategoryProduct.builder().build();
        CategoryAlreadyExistsException e = assertThrows(CategoryAlreadyExistsException.class,
                () -> validator.validateCategory(category));
        assertEquals("Nombre Existente: Hay registro de esta categoría.", e.getMessage());
    }

    //Prueba para validar el nombre de una subcategoría
    @ParameterizedTest
    @ValueSource(strings = {"arroz", "agua"})
    public void validateSubcategory_valid(String value){
        validator.validateSubcategory(value);
    }
    @ParameterizedTest
    @ValueSource(strings = {" ", "nn"})
    public void validateSubcategory_invalid(String value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateSubcategory(value));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"1223", "arroz2"})
    public void validateSubcategory_invalidNumeric(String value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateSubcategory(value));
        assertEquals("El nombre no debe contener dígitos.", exception.getMessage());
    }
    @Test
    public void validateSubcategory_invalidNull(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateSubcategory(null));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }

    //Prueba para validar una unidad de medida
    @Test
    public void validateUnit_validExistsException(){
        UnitProduct unit = UnitProduct.builder().build();
        UnitAlreadyExistsException e = assertThrows(UnitAlreadyExistsException.class,
                () -> validator.validateUnit(unit));
        assertEquals("Nombre Existente: Hay registro de esta unidad de medida.", e.getMessage());
    }

    //Prueba para validar el formato del nombre de la unidad de medida
    @ParameterizedTest
    @ValueSource(strings = {"2 kilo ", "1 unidad"})
    public void validateUnit_invalid(String value){
        UnitProduct unit = UnitProduct.builder().nameUnit(value).build();
        ValidationException exception = assertThrows(ValidationException.class,() -> validator.validateUnitName(unit));
        assertEquals("El nombre debe tener máximo 2 dígitos y 2 letras.", exception.getMessage());
    }
}

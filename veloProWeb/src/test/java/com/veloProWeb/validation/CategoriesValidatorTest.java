package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.BrandAlreadyExistsException;
import com.veloProWeb.exceptions.product.CategoryAlreadyExistsException;
import com.veloProWeb.model.entity.Product.BrandProduct;
import com.veloProWeb.model.entity.Product.CategoryProduct;
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

    //Prueba para validar el nombre de una unidad de medida
    @ParameterizedTest
    @ValueSource(strings = {"12 kg", "1 cm", "1 un"})
    public void validateUnit_valid(String value){
        validator.validateUnit(value);
    }
    @ParameterizedTest
    @ValueSource(strings = {"2 kilo ", "1 unidad", " "})
    public void validateUnit_invalid(String value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateUnit(value));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"kilogr@mo", "2@22"})
    public void validateUnit_invalidContainsLettersAndNumbers(String value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateUnit(value));
        assertEquals("El nombre debe contener solo letras y números.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"01cm", "20kg"})
    public void validateUnit_invalidContainsSpace(String value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateUnit(value));
        assertEquals("El nombre debe tener un espacio entre dígitos y letras.", exception.getMessage());
    }
    @Test
    public void validateUnit_invalidNull(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validateUnit(null));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }
}

package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.UnitAlreadyExistsException;
import com.veloProWeb.exceptions.validation.ValidationException;
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
public class UnitValidatorTest {

    @InjectMocks private UnitValidator validator;

    //Prueba para validar una unidad de medida
    @Test
    public void validateUnit_validExistsException(){
        UnitProduct unit = UnitProduct.builder().build();
        UnitAlreadyExistsException e = assertThrows(UnitAlreadyExistsException.class,
                () -> validator.validateUnitDoesNotExist(unit));
        assertEquals("Nombre Existente: Hay registro de esta unidad de medida.", e.getMessage());
    }

    //Prueba para validar el formato del nombre de la unidad de medida
    @ParameterizedTest
    @ValueSource(strings = {"2 kilo ", "1 unidad"})
    public void validateUnit_invalid(String value){
        UnitProduct unit = UnitProduct.builder().nameUnit(value).build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateUnitNameFormat(unit));
        assertEquals("El nombre debe tener máximo 2 dígitos y 2 letras.", exception.getMessage());
    }
}

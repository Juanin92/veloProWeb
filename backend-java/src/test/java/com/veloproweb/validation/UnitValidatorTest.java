package com.veloproweb.validation;

import com.veloproweb.exceptions.product.UnitAlreadyExistsException;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.entity.product.UnitProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UnitValidatorTest {

    @InjectMocks private UnitValidator validator;

    //Prueba para validar una unidad de medida
    @Test
    void validateUnit_ExistsException(){
        UnitProduct unit = UnitProduct.builder().build();
        UnitAlreadyExistsException e = assertThrows(UnitAlreadyExistsException.class,
                () -> validator.validateUnitDoesNotExist(unit));
        assertEquals("Nombre Existente: Hay registro de esta unidad de medida.", e.getMessage());
    }

    //Prueba para validar el formato del nombre de la unidad de medida
    @ParameterizedTest
    @ValueSource(strings = {"2 kilo ", "1 unidad"})
    void validateUnit_name(String value){
        UnitProduct unit = UnitProduct.builder().nameUnit(value).build();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateUnitNameFormat(unit));
        assertEquals("El nombre debe tener máximo 2 dígitos y 2 letras.", exception.getMessage());
    }
}

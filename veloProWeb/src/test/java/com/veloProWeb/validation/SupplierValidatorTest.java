package com.veloProWeb.validation;

import com.veloProWeb.model.entity.Purchase.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SupplierValidatorTest {
    @InjectMocks private SupplierValidator validator;
    private Supplier supplier;

    @BeforeEach
    void setUp(){
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Prueba");
        supplier.setEmail("x@x.xxx");
        supplier.setPhone("+569 12345678");
        supplier.setRut("12123123-6");
    }

    //Prueba para validar todos los datos
    @Test
    public void validateSupplier_valid(){
        validator.validate(supplier);
    }

    //Prueba para validar el nombre de un proveedor
    @ParameterizedTest
    @ValueSource(strings = {"Prueba", "Prueba1", "Prueba-123", " Prueba "})
    public void validateName_valid(String value){
        supplier.setName(value);
        validator.validate(supplier);
    }
    @Test
    public void validateName_invalidNull(){
        supplier.setName(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = { "", "  "})
    public void validateName_invalidEmpty(String value){
        supplier.setName(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"p", " pp "})
    public void validateName_invalidLength(String value){
        supplier.setName(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese un nombre válido.", exception.getMessage());
    }

    //Prueba para validar número de teléfono
    @Test
    public void validatePhone_invalidNull(){
        supplier.setPhone(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese número válido, Ej: +569 12345678", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = { "", "  "})
    public void validatePhone_invalidEmpty(String value){
        supplier.setPhone(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese número válido, Ej: +569 12345678", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"+569 123456789", "+569 123456"})
    public void validatePhone_invalidLength(String value){
        supplier.setPhone(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese número válido, Ej: +569 12345678", exception.getMessage());
    }

    //Prueba para validar un email de un proveedor
    @Test
    public void validateEmail_valid(){
        supplier.setEmail("example@gmail.com");
        validator.validate(supplier);
    }
    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    public void validateEmail_invalidEmpty(String value){
        supplier.setEmail(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese Email válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"example", "example@.com"})
    public void validateEmail_invalidNoMatches(String value){
        supplier.setEmail(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Ingrese Email válido.", exception.getMessage());
    }

    //Prueba para validar rut de un proveedor
    @Test
    public void validateRut_invalidEmpty(){
        supplier.setRut(" ");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Rut no es correcto, Ingrese un formato válido", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"example", "123434-4"})
    public void validateRut_invalidNoMatches(String value){
        supplier.setRut(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(supplier));
        assertEquals("Rut no es correcto, Ingrese un formato válido", exception.getMessage());
    }
}

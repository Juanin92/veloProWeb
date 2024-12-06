package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CostumerValidatorTest {
    @InjectMocks private CostumerValidator validator = new CostumerValidator();
    private Costumer costumer;

    @BeforeEach
    void setUp(){
        costumer = new Costumer(1L,"Juan", "Perez", "+569 12345678", "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
    }

    //Prueba para validar todos los datos válidos
    @Test
    public void validateCostumer_valid(){
        validator.validate(costumer);
    }

    //Pruebas para validar nombre del cliente
    @ParameterizedTest
    @ValueSource(strings = {" ", "Ju", "Ju123"})
    void validateName_invalidNames(String name){
        costumer.setName(name);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(costumer));
        assertEquals("Ingrese nombre válido.", exception.getMessage());
    }

    @Test
    public void validateName_invalidNameNull(){
        costumer.setName(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(costumer));
        assertEquals("Ingrese nombre válido.", exception.getMessage());
    }

    //Pruebas para validar apellido del cliente
    @ParameterizedTest
    @ValueSource(strings = {"","Pr","Perez2323"})
    public void validateSurname_invalidSurnames(String surname){
        costumer.setSurname(surname);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(costumer));
        assertEquals("Ingrese apellido válido.", exception.getMessage());
    }
    @Test
    public void validateSurname_invalidSurnameNull(){
        costumer.setSurname(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(costumer));
        assertEquals("Ingrese apellido válido.", exception.getMessage());
    }

    //Pruebas para validar Teléfono del cliente
    @ParameterizedTest
    @ValueSource(strings = {"","+569 12345","+569 123456789"})
    public void validatePhone_invalidPhone(String phone){
        costumer.setPhone(phone);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(costumer));
        assertEquals("Ingrese número válido, Ej: +569 12345678", exception.getMessage());
    }

    //Pruebas para validar Email del cliente
    @ParameterizedTest
    @ValueSource(strings = {"","asdfs"})
    public void validateEmail_invalidEmail(String email){
        costumer.setEmail(email);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(costumer));
        assertEquals("Ingrese Email válido.", exception.getMessage());
    }

    //Pruebas para validar el monto del pago de un abono asociado a un cliente
    @Test
    public void validateValuePayment_valid(){
        int number = 10000;
        costumer.setDebt(20000);
        validator.validateValuePayment(number,costumer);
    }
    @Test
    public void validateValuePayment_invalidNumber(){
        int number = 0;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateValuePayment(number, costumer));
        assertEquals("El monto no puede ser menor a 0.", exception.getMessage());
    }
    @Test
    public void validateValuePayment_invalidDebt(){
        int number = 20000;
        costumer.setDebt(1000);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateValuePayment(number, costumer));
        assertEquals("El monto supera el valor de la deuda.", exception.getMessage());
    }
}
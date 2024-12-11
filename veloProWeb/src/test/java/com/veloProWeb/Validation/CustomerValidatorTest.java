package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Customer.Customer;
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
public class CustomerValidatorTest {
    @InjectMocks private CustomerValidator validator = new CustomerValidator();
    private Customer customer;

    @BeforeEach
    void setUp(){
        customer = new Customer(1L,"Juan", "Perez Perez", "+569 12345678", "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
    }

    //Prueba para validar todos los datos válidos
    @Test
    public void validateCustomer_valid(){
        validator.validate(customer);
    }

    //Pruebas para validar nombre del cliente
    @ParameterizedTest
    @ValueSource(strings = {" ", "Ju", "Ju123"})
    void validateName_invalidNames(String name){
        customer.setName(name);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese nombre válido.", exception.getMessage());
    }

    @Test
    public void validateName_invalidNameNull(){
        customer.setName(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese nombre válido.", exception.getMessage());
    }

    //Pruebas para validar apellido del cliente
    @ParameterizedTest
    @ValueSource(strings = {"","Pr","Perez2323"})
    public void validateSurname_invalidSurnames(String surname){
        customer.setSurname(surname);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese apellido válido.", exception.getMessage());
    }
    @Test
    public void validateSurname_invalidSurnameNull(){
        customer.setSurname(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese apellido válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"perez", "perez perez perez"})
    public void validateSurname_invalidNormalSurname(String surname){
        customer.setSurname(surname);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese los 2 apellidos", exception.getMessage());
    }

    //Pruebas para validar Teléfono del cliente
    @ParameterizedTest
    @ValueSource(strings = {"","+569 12345","+569 123456789"})
    public void validatePhone_invalidPhone(String phone){
        customer.setPhone(phone);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese número válido, Ej: +569 12345678", exception.getMessage());
    }

    //Pruebas para validar Email del cliente
    @ParameterizedTest
    @ValueSource(strings = {"","asdf"})
    public void validateEmail_invalidEmail(String email){
        customer.setEmail(email);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(customer));
        assertEquals("Ingrese Email válido.", exception.getMessage());
    }

    //Pruebas para validar el monto del pago de un abono asociado a un cliente
    @Test
    public void validateValuePayment_valid(){
        int number = 10000;
        customer.setDebt(20000);
        validator.validateValuePayment(number, customer);
    }
    @Test
    public void validateValuePayment_invalidNumber(){
        int number = 0;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateValuePayment(number, customer));
        assertEquals("El monto no puede ser menor a 0.", exception.getMessage());
    }
    @Test
    public void validateValuePayment_invalidDebt(){
        int number = 20000;
        customer.setDebt(1000);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateValuePayment(number, customer));
        assertEquals("El monto supera el valor de la deuda.", exception.getMessage());
    }
}
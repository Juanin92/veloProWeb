package com.veloproweb.validation;

import com.veloproweb.exceptions.customer.CustomerAlreadyActivatedException;
import com.veloproweb.exceptions.customer.CustomerAlreadyDeletedException;
import com.veloproweb.exceptions.customer.CustomerAlreadyExistsException;
import com.veloproweb.exceptions.customer.InvalidPaymentAmountException;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.Enum.PaymentStatus;
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
        customer = new Customer(1L,"Juan", "Perez Perez", "+569 12345678",
                "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(),
                new ArrayList<>());
    }

    //Prueba para saber la existencia de un cliente en la DB
    @Test
    void validateExistence_exception(){
        CustomerAlreadyExistsException e = assertThrows(CustomerAlreadyExistsException.class,
                ()-> validator.existCustomer(customer));
        assertEquals("Cliente Existente: Hay registro de este cliente.", e.getMessage());
    }
    //Pruebas para validar apellido del cliente
    @ParameterizedTest
    @ValueSource(strings = {"Jackson","Doe","Perez"})
    void validateSurname_invalidLong(String surname){
        customer.setSurname(surname);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateInfoCustomer(customer));
        assertEquals("Ingrese los 2 apellidos", exception.getMessage());
    }

    //Prueba para validar eliminación de cliente
    @Test
    void validateDelete_accountException(){
        customer.setAccount(false);
        CustomerAlreadyDeletedException e = assertThrows(CustomerAlreadyDeletedException.class,
                ()-> validator.deleteCustomer(customer));
        assertEquals("Cliente ya ha sido eliminado anteriormente.", e.getMessage());
    }
    @Test
    void validateDelete_hasDebtException(){
        customer.setDebt(2000);
        ValidationException e = assertThrows(ValidationException.class,
                ()-> validator.deleteCustomer(customer));
        assertEquals("El cliente tiene deuda pendiente, no se puede eliminar.", e.getMessage());
    }

    //Prueba para validar activación de cliente
    @Test
    void validateActivate_isActiveException(){
        CustomerAlreadyActivatedException e = assertThrows(CustomerAlreadyActivatedException.class,
                ()-> validator.isActive(customer));
        assertEquals("El cliente tiene su cuenta activada", e.getMessage());
    }

    //Prueba para validar email
    @Test
    void validateEmail_blankException(){
        customer.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateInfoCustomer(customer));
        assertEquals("Ingrese Email válido.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"test", "test@com", "test.com", "test@.com", "test@com."})
    void validateEmail_notMatchException(String email){
        customer.setEmail(email);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateInfoCustomer(customer));
        assertEquals("Ingrese Email válido.", exception.getMessage());
    }

    //Pruebas para validar el monto del pago de un abono asociado a un cliente
    @Test
    void validateValuePayment_valid(){
        int number = 10000;
        customer.setDebt(20000);
        validator.validateValuePayment(number, customer);
    }
    @Test
    void validateValuePayment_invalidNumber(){
        int number = 0;
        InvalidPaymentAmountException exception = assertThrows(InvalidPaymentAmountException.class,
                () -> validator.validateValuePayment(number, customer));
        assertEquals("El monto no puede ser menor a 0.", exception.getMessage());
    }
    @Test
    void validateValuePayment_invalidDebt(){
        int number = 20000;
        customer.setDebt(1000);
        InvalidPaymentAmountException exception = assertThrows(InvalidPaymentAmountException.class,
                () -> validator.validateValuePayment(number, customer));
        assertEquals("El monto supera el valor de la deuda.", exception.getMessage());
    }
}
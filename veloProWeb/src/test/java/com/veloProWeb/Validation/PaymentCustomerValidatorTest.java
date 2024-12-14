package com.veloProWeb.Validation;

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
public class PaymentCustomerValidatorTest {

    @InjectMocks private PaymentCustomerValidator validator;
    private String amount;
    private String comment;

    @BeforeEach
    void setUp(){
        amount = "1000";
        comment = "Prueba";
    }

    //Pruebas para validar abono de una deuda
    @Test
    public void validatePayment_valid(){
        validator.validatePayment(amount, comment);
    }

    //Prueba para validar el monto del abono
    @Test
    public void validatePaymentAmount_invalid(){
        amount = "test";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validatePayment(amount, comment));
        assertEquals("Ingrese solo números.", exception.getMessage());
    }

    //Pruebas para validar el comentario del proceso de abono
    @Test
    public void validateComment_invalidEmptyValue(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validatePayment(amount, " "));
        assertEquals("Seleccione una forma de pago.", exception.getMessage());
    }
    @Test
    public void validateComment_invalidNullValue(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validatePayment(amount, null));
        assertEquals("Seleccione una forma de pago.", exception.getMessage());
    }
}

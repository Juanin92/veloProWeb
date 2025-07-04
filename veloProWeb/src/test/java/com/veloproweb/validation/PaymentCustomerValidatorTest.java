package com.veloproweb.validation;

import com.veloproweb.exceptions.customer.InvalidPaymentAmountException;
import com.veloproweb.exceptions.customer.NoTicketSelectedException;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.entity.customer.TicketHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentCustomerValidatorTest {

    @InjectMocks private PaymentCustomerValidator validator;
    private TicketHistory ticketHistory;
    private String amount;
    private String comment;

    @BeforeEach
    void setUp(){
        amount = "1000";
        comment = "Prueba";

        ticketHistory = TicketHistory.builder()
                .id(1L)
                .document("BOLETA_1")
                .total(1000)
                .date(LocalDate.now())
                .notificationsDate(null)
                .customer(null)
                .build();
    }

    //Pruebas para validar abono de una deuda
    @Test
    void validatePayment_valid(){
        validator.validatePayment(amount, comment);
    }

    //Prueba para validar el monto del abono
    @Test
    void validatePaymentAmount_invalid(){
        amount = "test";
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validatePayment(amount, comment));
        assertEquals("Ingrese solo números.", exception.getMessage());
    }

    //Pruebas para validar el comentario del proceso de abono
    @Test
    void validateComment_invalidEmptyValue(){
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validatePayment(amount, " "));
        assertEquals("Seleccione una forma de pago.", exception.getMessage());
    }
    @Test
    void validateComment_invalidNullValue(){
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validatePayment(amount, null));
        assertEquals("Seleccione una forma de pago.", exception.getMessage());
    }

    //Prueba para validar lista de boletas seleccionadas
    @Test
    void validateTickets_valid(){
        List<TicketHistory> tickets = List.of(ticketHistory);
        validator.validateTickets(tickets);
    }
    @Test
    void validateTickets_validException(){
        List<TicketHistory> tickets = Collections.emptyList();
        NoTicketSelectedException e = assertThrows(NoTicketSelectedException.class,
                () -> validator.validateTickets(tickets));
        assertEquals("No ha seleccionado ninguna boleta", e.getMessage());
    }

    //Prueba para validar monto de abono no exceda la deuda
    @Test
    void validatePaymentNotExceedDebt_valid(){
        validator.validatePaymentNotExceedDebt(1000, 5000);
    }
    @Test
    void validatePaymentNotExceedDebt_validException(){
        InvalidPaymentAmountException e = assertThrows(InvalidPaymentAmountException.class,
                () -> validator.validatePaymentNotExceedDebt(5000, 1000));
        assertEquals("El monto supera el valor de la deuda.", e.getMessage());
    }

    //Prueba para validar monto de abono similar al total de boletas seleccionadas
    @Test
    void validatePaymentsForMultipleTickets_valid(){
        List<TicketHistory> tickets = List.of(ticketHistory);
        validator.validateExactPaymentForMultipleTickets(1000, tickets);
    }
    @Test
    void validatePaymentsForMultipleTickets_validException(){
        List<TicketHistory> tickets = List.of(ticketHistory);
        InvalidPaymentAmountException e = assertThrows(InvalidPaymentAmountException.class,
                () -> validator.validateExactPaymentForMultipleTickets(5000, tickets));
        assertEquals("El monto no es correcto para el pago de la deuda", e.getMessage());
    }
}

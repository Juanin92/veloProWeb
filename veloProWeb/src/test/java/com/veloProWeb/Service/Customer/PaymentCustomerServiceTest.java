package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Repository.Customer.PaymentCustomerRepo;
import com.veloProWeb.Validation.PaymentCustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCustomerServiceTest {
    @InjectMocks private PaymentCustomerService paymentCustomerService;
    @Mock private PaymentCustomerRepo paymentCustomerRepo;
    @Mock private PaymentCustomerValidator validator;
    @Mock private TicketHistory ticketHistory;
    @Mock private Customer customer;
    private PaymentCustomer paymentCustomer;

    @BeforeEach
    void setUp(){
        paymentCustomer = new PaymentCustomer(1L,1000,"Test Comment", LocalDate.now(), customer, ticketHistory);
    }

    //Prueba para obtener una lista de todos los abonos
    @Test
    public void getAll_valid(){
        paymentCustomerService.getAll();
        verify(paymentCustomerRepo).findAll();
    }
    @Test
    public void getAll_validGetValue(){
        List<PaymentCustomer> mockPayment = Collections.singletonList(paymentCustomer);
        when(paymentCustomerRepo.findAll()).thenReturn(mockPayment);

        List<PaymentCustomer> result = paymentCustomerService.getAll();
        verify(paymentCustomerRepo).findAll();
        assertEquals(mockPayment, result);
    }

    //Prueba para obtener una lista de las deudas del cliente seleccionado
    @Test
    public void getCustomerSelected_valid(){
        paymentCustomerService.getCustomerSelected(1L);
        verify(paymentCustomerRepo).findByCustomerId(1L);
    }
    @Test
    public void getCustomerSelected_validValue(){
        List<PaymentCustomer> mockPayment = Collections.singletonList(paymentCustomer);
        when(paymentCustomerRepo.findByCustomerId(1L)).thenReturn(mockPayment);

        List<PaymentCustomer> result = paymentCustomerService.getCustomerSelected(1L);
        verify(paymentCustomerRepo).findByCustomerId(1L);
        assertEquals(1, result.size());
        assertEquals(mockPayment, result);
    }
}

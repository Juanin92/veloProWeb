package com.veloProWeb.Controller.Customer;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Service.Customer.Interfaces.IPaymentCustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PaymentCustomerControllerTest {
    @InjectMocks private PaymentCustomerController paymentCustomerController;
    @Mock private IPaymentCustomerService paymentCustomerService;
    @Autowired private MockMvc mockMvc;
    private PaymentCustomer paymentCustomer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentCustomerController).setControllerAdvice(new GlobalExceptionHandler()).build();
        Customer customer = new Customer();
        TicketHistory ticketHistory = new TicketHistory();
        customer.setId(1L);
        ticketHistory.setId(1L);
        paymentCustomer = new PaymentCustomer(1L,1000,"Test Comment", LocalDate.now(), customer, ticketHistory);
    }

    //Prueba para obtener todos los pagos de abono
    @Test
    public void getAllPaymentNull_valid() throws Exception {
        when(paymentCustomerService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(paymentCustomerService, times(1)).getAll();
    }
    @Test
    public void getAllPaymentData_valid() throws Exception {
        List<PaymentCustomer> paymentCustomerList = Collections.singletonList(paymentCustomer);
        when(paymentCustomerService.getAll()).thenReturn(paymentCustomerList);
        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(paymentCustomerService, times(1)).getAll();
    }
    @Test
    public void getAllPayment_error() throws Exception {
        when(paymentCustomerService.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/pagos"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(paymentCustomerService, times(1)).getAll();
    }

    //Prueba para obtener todos los pagos de abono de cliente seleccionado
    @Test
    public void getCustomerSelectedPayment_valid() throws Exception {
        Long customerID = 1L;
        List<PaymentCustomer> paymentCustomerList = Collections.singletonList(paymentCustomer);
        when(paymentCustomerService.getCustomerSelected(customerID)).thenReturn(paymentCustomerList);
        mockMvc.perform(get("/pagos/abonos").param("customerId", String.valueOf(customerID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(paymentCustomerService, times(1)).getCustomerSelected(customerID);
    }
    @Test
    public void getCustomerSelectedPayment_error() throws Exception {
        Long customerID = 10L;
        when(paymentCustomerService.getCustomerSelected(customerID)).thenThrow(new IllegalArgumentException("ID no válido"));
        mockMvc.perform(get("/pagos/abonos").param("customerId", String.valueOf(customerID)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID no válido"));
        verify(paymentCustomerService, times(1)).getCustomerSelected(customerID);
    }
}

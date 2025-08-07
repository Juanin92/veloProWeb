package com.veloproweb.service.sale;

import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.enums.PaymentMethod;
import com.veloproweb.service.customer.interfaces.ICustomerService;
import com.veloproweb.service.customer.interfaces.ITicketHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleEventServiceTest {

    @InjectMocks private SaleEventService saleEventService;
    @Mock private ICustomerService customerService;
    @Mock private ITicketHistoryService ticketHistoryService;


    //Prueba para crear una venta con método de pago de formato préstamo
    @Test
    void createSaleLoanPaymentEvent() {
        int amount = 300;
        Customer customer = Customer.builder().id(1L).totalDebt(0).debt(0).build();

        saleEventService.createSaleLoanPaymentEvent(customer, amount);

        verify(customerService, times(1)).updateTotalDebt(customer);
        verify(customerService, times(1)).addSaleToCustomer(customer);
        verify(ticketHistoryService, times(1)).addTicketToCustomer(customer, amount);

        assertEquals(customer.getDebt() + amount, customer.getTotalDebt());
    }

    //Prueba para crear una venta con método de pago de formato mixto
    @Test
    void createSaleMixPaymentEvent() {
        int amount = 300;
        Customer customer = Customer.builder().id(1L).totalDebt(0).debt(0).build();

        saleEventService.createSaleMixPaymentEvent(customer, amount);

        verify(customerService, times(1)).updateTotalDebt(customer);
        verify(customerService, times(1)).addSaleToCustomer(customer);
        verify(ticketHistoryService, times(1)).addTicketToCustomer(customer, amount);

        assertEquals(customer.getDebt() + amount, customer.getTotalDebt());
    }

    //Prueba para verificar si se necesita un cliente para el método de pago
    @Test
    void needsCustomer_loanPaymentMethod() {
        Customer customer = Customer.builder().id(1L).build();
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        Customer result = saleEventService.needsCustomer(PaymentMethod.PRESTAMO, 1L);

        verify(customerService, times(1)).getCustomerById(1L);

        assertEquals(result.getId(), customer.getId());
    }
    @Test
    void needsCustomer_mixedPaymentMethod() {
        Customer customer = Customer.builder().id(1L).build();
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        Customer result = saleEventService.needsCustomer(PaymentMethod.MIXTO, 1L);

        verify(customerService, times(1)).getCustomerById(1L);

        assertEquals(result.getId(), customer.getId());
    }
    @Test
    void needsCustomer_differentPaymentMethod() {
        Customer result = saleEventService.needsCustomer(PaymentMethod.EFECTIVO, 1L);

        verify(customerService, never()).getCustomerById(1L);

        assertNull(result);
    }
}
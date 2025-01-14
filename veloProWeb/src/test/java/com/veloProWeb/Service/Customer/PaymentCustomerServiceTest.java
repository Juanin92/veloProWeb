package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.DTO.PaymentRequestDTO;
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
    @Mock private TicketHistoryService ticketService;
    @Mock private CustomerService customerService;
    @Mock private TicketHistory ticketHistory;
    private PaymentCustomer paymentCustomer;
    private PaymentRequestDTO dto;
    private TicketHistory ticketHistoryReal;
    private TicketHistory ticketHistoryReal2;
    private Customer realCustomer;

    @BeforeEach
    void setUp(){
        paymentCustomer = new PaymentCustomer(1L,1000,"Test Comment", LocalDate.now(), realCustomer, ticketHistory);
        realCustomer = new Customer();
        realCustomer.setId(1L);
        ticketHistoryReal = new TicketHistory();
        ticketHistoryReal.setId(1L);
        ticketHistoryReal.setCustomer(realCustomer);
        ticketHistoryReal.setTotal(5000);
        ticketHistoryReal2 = new TicketHistory();
        ticketHistoryReal2.setId(2L);
        ticketHistoryReal2.setCustomer(realCustomer);
        ticketHistoryReal2.setTotal(5000);
        dto = new PaymentRequestDTO();
        dto.setAmount(10000);
        dto.setCustomer(realCustomer);
        dto.setComment("Efectivo");
        dto.setTotalPaymentPaid(0);
    }

    //Prueba para crear un proceso de pagos de tickets
    @Test
    public void createPaymentProcess_validMultiplesTickets(){
        dto.setTickets(Arrays.asList(ticketHistoryReal,ticketHistoryReal2));
        paymentCustomerService.createPaymentProcess(dto);

        verify(ticketService, times(1)).updateStatus(ticketHistoryReal);
        verify(ticketService, times(1)).updateStatus(ticketHistoryReal2);
        verify(customerService, times(2)).paymentDebt(eq(realCustomer), eq("5000"));
    }
    @Test
    public void createPaymentProcess_invalidAmountThrowsException() {
        dto.setTickets(Arrays.asList(ticketHistoryReal, ticketHistoryReal2));
        dto.setAmount(8000);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentCustomerService.createPaymentProcess(dto)
        );
        assertEquals("El monto no es correcto para el pago de la deuda", exception.getMessage());
        verifyNoInteractions(customerService);
    }
//    @Test
//    public void createPaymentProcess_adjustPaymentForSingleTicket() {
//        dto.setAmount(3000);
//        dto.setTotalPaymentPaid(2000);
//        dto.setTickets(Collections.singletonList(ticketHistoryReal));
//        paymentCustomerService.createPaymentProcess(dto);
//
//        verify(paymentCustomerRepo, times(1)).save(any(PaymentCustomer.class));
//        verify(customerService, times(1)).paymentDebt(realCustomer, "3000");
//        verifyNoInteractions(ticketService);
//    }
    @Test
    public void createPaymentProcess_emptyTicketListThrowsException() {
        dto.setTickets(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentCustomerService.createPaymentProcess(dto)
        );
        assertEquals("No ha seleccionado ninguna boleta", exception.getMessage());
        verifyNoInteractions(customerService, ticketService, paymentCustomerRepo);
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
//    @Test
//    public void getCustomerSelected_validValue(){
//        List<PaymentCustomer> mockPayment = Collections.singletonList(paymentCustomer);
//        when(paymentCustomerRepo.findByCustomerId(1L)).thenReturn(mockPayment);
//
//        List<PaymentCustomer> result = paymentCustomerService.getCustomerSelected(1L);
//        verify(paymentCustomerRepo).findByCustomerId(1L);
//        assertEquals(1, result.size());
//        assertEquals(mockPayment, result);
//    }
}

package com.veloproweb.service.customer;

import com.veloproweb.exceptions.customer.InvalidPaymentAmountException;
import com.veloproweb.mapper.PaymentCustomerMapper;
import com.veloproweb.model.dto.customer.PaymentRequestDTO;
import com.veloproweb.model.dto.customer.PaymentResponseDTO;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.entity.customer.PaymentCustomer;
import com.veloproweb.model.entity.customer.TicketHistory;
import com.veloproweb.repository.customer.PaymentCustomerRepo;
import com.veloproweb.validation.PaymentCustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCustomerServiceTest {
    @InjectMocks private PaymentCustomerService paymentCustomerService;
    @Mock private PaymentCustomerRepo paymentCustomerRepo;
    @Mock private TicketHistoryService ticketService;
    @Mock private CustomerService customerService;
    @Spy private PaymentCustomerValidator validator;
    @Spy private PaymentCustomerMapper mapper = new PaymentCustomerMapper();
    private PaymentCustomer paymentCustomer;
    private PaymentRequestDTO dto;
    private TicketHistory ticketFirst, ticketSecond;
    private Customer customer;

    @BeforeEach
    void setUp(){
        customer = Customer.builder().id(1L).name("John").surname("Doe").build();
        ticketFirst = TicketHistory.builder()
                .id(1L).total(5000).customer(customer).document("Ticket_1").build();
        ticketSecond = TicketHistory.builder()
                .id(2L).total(5000).customer(customer).document("Ticket_2").build();
        dto = PaymentRequestDTO.builder()
                .amount(10000)
                .customerID(customer.getId())
                .comment("Efectivo")
                .totalPaymentPaid(0).build();
        paymentCustomer = PaymentCustomer.builder()
                .id(1L).amount(1000).comment("Test Comment")
                .date(LocalDate.now()).customer(customer)
                .document(ticketFirst).build();
    }

    //Prueba para obtener una lista de todos los abonos
    @Test
    void getAll_valid(){
        PaymentResponseDTO paymentResponseDTO = mapper.toDto(paymentCustomer);
        List<PaymentResponseDTO> dtoList = Collections.singletonList(paymentResponseDTO);
        List<PaymentCustomer> paymentCustomers = Collections.singletonList(paymentCustomer);

        when(paymentCustomerRepo.findAll()).thenReturn(paymentCustomers);

        List<PaymentResponseDTO> result = paymentCustomerService.getAll();

        verify(paymentCustomerRepo, times(1)).findAll();
        assertEquals(result.size(), dtoList.size());
        assertEquals(result.getFirst(), dtoList.getFirst());
        assertEquals("Test Comment", result.getFirst().getComment());
        assertEquals(1000, result.getFirst().getAmount());
        assertEquals("Ticket_1", result.getFirst().getDocument());
    }

    //Prueba para crear un proceso de pagos de tickets
    @Test
    void createPaymentProcess_validMultiplesTickets(){
        dto.setTicketIDs(List.of(ticketFirst.getId(), ticketSecond.getId()));

        when(ticketService.getTicketById(ticketFirst.getId())).thenReturn(ticketFirst);
        when(ticketService.getTicketById(ticketSecond.getId())).thenReturn(ticketSecond);

        paymentCustomerService.createPaymentProcess(dto);

        ArgumentCaptor<PaymentCustomer> paymentCaptor = ArgumentCaptor.forClass(PaymentCustomer.class);
        verify(ticketService, times(1)).updateStatus(ticketFirst);
        verify(ticketService, times(1)).updateStatus(ticketSecond);
        verify(paymentCustomerRepo, times(2)).save(paymentCaptor.capture());

        List<PaymentCustomer> savedPayments = paymentCaptor.getAllValues();
        PaymentCustomer firstSaved = savedPayments.getFirst();
        PaymentCustomer secondSaved = savedPayments.get(1);
        assertEquals(2, savedPayments.size());
        assertEquals(ticketFirst.getCustomer(), firstSaved.getCustomer());
        assertEquals(ticketFirst.getTotal(), firstSaved.getAmount());
        assertEquals(ticketFirst, firstSaved.getDocument());
        assertEquals("Efectivo", firstSaved.getComment());
        assertEquals(ticketSecond.getCustomer(), secondSaved.getCustomer());
        assertEquals(ticketSecond.getTotal(), secondSaved.getAmount());
        assertEquals(ticketSecond, secondSaved.getDocument());
    }
    @Test
    void createPaymentProcess_invalidPaymentAmountException() {
        dto.setAmount(800);
        dto.setTicketIDs(Arrays.asList(ticketFirst.getId(), ticketSecond.getId()));

        when(ticketService.getTicketById(1L)).thenReturn(ticketFirst);
        when(ticketService.getTicketById(2L)).thenReturn(ticketSecond);
        doThrow(new InvalidPaymentAmountException("El monto no es correcto para el pago de la deuda")).when(validator)
                .validateExactPaymentForMultipleTickets(dto.getAmount(), Arrays.asList(ticketFirst, ticketSecond));

        InvalidPaymentAmountException exception = assertThrows(
                InvalidPaymentAmountException.class, () -> paymentCustomerService.createPaymentProcess(dto));

        verify(ticketService, never()).updateStatus(ticketFirst);
        verify(ticketService, never()).updateStatus(ticketSecond);
        verify(paymentCustomerRepo, never()).save(paymentCustomer);
        assertEquals("El monto no es correcto para el pago de la deuda", exception.getMessage());
    }

    //Prueba para crear un proceso de pago de un solo ticket
    @Test
    void createPaymentProcess_validSingleTicket(){
        dto.setAmount(5000);
        ticketFirst.setTotal(5000);
        dto.setTicketIDs(List.of(ticketFirst.getId()));

        when(ticketService.getTicketById(ticketFirst.getId())).thenReturn(ticketFirst);
        when(customerService.getCustomerById(dto.getCustomerID())).thenReturn(customer);

        paymentCustomerService.createPaymentProcess(dto);

        ArgumentCaptor<PaymentCustomer> paymentCaptor = ArgumentCaptor.forClass(PaymentCustomer.class);
        verify(ticketService, times(1)).updateStatus(ticketFirst);
        verify(paymentCustomerRepo, times(1)).save(paymentCaptor.capture());

        List<PaymentCustomer> savedPayments = paymentCaptor.getAllValues();
        PaymentCustomer firstSaved = savedPayments.getFirst();
        assertEquals(1, savedPayments.size());
        assertEquals(ticketFirst.getCustomer(), firstSaved.getCustomer());
        assertEquals(ticketFirst.getTotal(), firstSaved.getAmount());
        assertEquals(ticketFirst, firstSaved.getDocument());
        assertEquals("Efectivo", firstSaved.getComment());
    }
    @Test
    void createPaymentProcess_validatePaymentNotExceedDebt(){
        dto.setAmount(10000);
        ticketFirst.setTotal(5000);
        dto.setTicketIDs(List.of(ticketFirst.getId()));

        when(ticketService.getTicketById(ticketFirst.getId())).thenReturn(ticketFirst);
        when(customerService.getCustomerById(dto.getCustomerID())).thenReturn(customer);
        doThrow(new InvalidPaymentAmountException("El monto supera el valor de la deuda.")).when(validator)
                        .validatePaymentNotExceedDebt(dto.getAmount(), ticketFirst.getTotal());

        InvalidPaymentAmountException e = assertThrows(InvalidPaymentAmountException.class,
                () -> paymentCustomerService.createPaymentProcess(dto));

        verify(ticketService, never()).updateStatus(ticketFirst);
        verify(paymentCustomerRepo, never()).save(paymentCustomer);

        assertEquals("El monto supera el valor de la deuda.", e.getMessage());
    }

    //Prueba para crear un proceso de pago de un ticket con ajuste de deuda
    @Test
    void createPaymentProcess_adjustPaymentForSingleTicket() {
        dto.setAmount(1000);
        dto.setTotalPaymentPaid(3000);
        dto.setTicketIDs(List.of(ticketFirst.getId()));

        when(customerService.getCustomerById(dto.getCustomerID())).thenReturn(customer);
        when(ticketService.getTicketById(ticketFirst.getId())).thenReturn(ticketFirst);

        paymentCustomerService.createPaymentProcess(dto);

        ArgumentCaptor<PaymentCustomer> paymentCaptor = ArgumentCaptor.forClass(PaymentCustomer.class);
        verify(customerService, times(1)).getCustomerById(dto.getCustomerID());
        verify(paymentCustomerRepo, times(1)).save(paymentCaptor.capture());

        List<PaymentCustomer> savedPayments = paymentCaptor.getAllValues();
        PaymentCustomer firstSaved = savedPayments.getFirst();
        int remainingDebt = (ticketFirst.getTotal() - dto.getTotalPaymentPaid());
        assertEquals(1, savedPayments.size());
        assertEquals(ticketFirst.getCustomer(), firstSaved.getCustomer());
        assertEquals(remainingDebt - dto.getAmount(), firstSaved.getAmount());
        assertEquals(ticketFirst, firstSaved.getDocument());
        assertEquals("Ajuste", firstSaved.getComment());
    }

    //Prueba para obtener una lista de las deudas del cliente seleccionado
    @Test
    void getCustomerSelected_validSingleTicket(){
        List<PaymentResponseDTO> responseDTOS = Collections.singletonList(mapper.toDto(paymentCustomer));
        List<TicketHistory> tickets = Collections.singletonList(ticketFirst);
        List<PaymentCustomer> paymentCustomers = Collections.singletonList(paymentCustomer);

        when(ticketService.getByCustomerId(customer.getId())).thenReturn(tickets);
        when(paymentCustomerRepo.findByCustomerId(customer.getId())).thenReturn(paymentCustomers);

        List<PaymentResponseDTO> result = paymentCustomerService.getCustomerSelected(customer.getId());

        verify(ticketService, times(1)).getByCustomerId(customer.getId());
        verify(paymentCustomerRepo, times(1)).findByCustomerId(customer.getId());

        assertEquals(result.size(), responseDTOS.size());
        assertEquals(result.getFirst().getDocument(), responseDTOS.getFirst().getDocument());
    }
    @Test
    void getCustomerSelected_validMultipleTickets(){
        PaymentCustomer anotherPayment = PaymentCustomer.builder()
                .id(2L).amount(2000).comment("Another Comment")
                .date(LocalDate.now().plusDays(1)).customer(customer)
                .document(ticketSecond).build();
        List<PaymentResponseDTO> responseDTOS = List.of(mapper.toDto(paymentCustomer), mapper.toDto(anotherPayment));
        List<TicketHistory> tickets = List.of(ticketFirst, ticketSecond);
        List<PaymentCustomer> paymentCustomers = List.of(paymentCustomer, anotherPayment);

        when(ticketService.getByCustomerId(customer.getId())).thenReturn(tickets);
        when(paymentCustomerRepo.findByCustomerId(customer.getId())).thenReturn(paymentCustomers);

        List<PaymentResponseDTO> result = paymentCustomerService.getCustomerSelected(customer.getId());

        verify(ticketService, times(1)).getByCustomerId(customer.getId());
        verify(paymentCustomerRepo, times(1)).findByCustomerId(customer.getId());

        assertEquals(result.size(), responseDTOS.size());
        assertEquals(result.getFirst().getDocument(), responseDTOS.getFirst().getDocument());
        assertEquals(result.getLast().getDocument(), responseDTOS.getLast().getDocument());
    }
    @Test
    void getCustomerSelected_validEmptyTickets(){
        TicketHistory anotherTicket = TicketHistory.builder().id(3L).customer(customer).document("Ticket_3").build();
        List<TicketHistory> tickets = List.of(anotherTicket);
        List<PaymentCustomer> paymentCustomers = List.of(paymentCustomer);

        when(ticketService.getByCustomerId(customer.getId())).thenReturn(tickets);
        when(paymentCustomerRepo.findByCustomerId(customer.getId())).thenReturn(paymentCustomers);

        List<PaymentResponseDTO> result = paymentCustomerService.getCustomerSelected(customer.getId());

        verify(ticketService, times(1)).getByCustomerId(customer.getId());
        verify(paymentCustomerRepo, times(1)).findByCustomerId(customer.getId());

        assertTrue(result.isEmpty());
    }
}

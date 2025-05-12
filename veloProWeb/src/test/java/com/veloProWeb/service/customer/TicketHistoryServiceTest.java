package com.veloProWeb.service.customer;

import com.veloProWeb.exceptions.Customer.TicketAlreadyPaidException;
import com.veloProWeb.exceptions.Customer.TicketNotFoundException;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.repository.customer.TicketHistoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketHistoryServiceTest {

    @InjectMocks private TicketHistoryService ticketHistoryService;
    @Mock private TicketHistoryRepo ticketHistoryRepo;
    @Mock private Customer customers;
    @Mock private CustomerService customerService;
    private TicketHistory ticketHistory, ticketHistoryFirst, ticketHistorySecond, ticketHistoryThird;
    private LocalDate lastValidationDate;
    private Customer customer;

    @BeforeEach
    void setUp(){
        customer  = Customer.builder()
                .id(1L).name("John").surname("Doe").build();
        ticketHistory = TicketHistory.builder()
                .id(1L).document("BO001").total(2000).status(false).notificationsDate(LocalDate.now())
                .date(LocalDate.now()).customer(customer).build();
        ticketHistoryFirst = TicketHistory.builder()
                .id(2L).document("BO002").total(2000).status(false).notificationsDate(LocalDate.now())
                .date(LocalDate.now()).customer(customer).build();
        ticketHistorySecond = TicketHistory.builder()
                .id(3L).document("BO003").total(2000).status(true).notificationsDate(LocalDate.now())
                .date(LocalDate.now()).customer(customer).build();
        ticketHistoryThird = TicketHistory.builder()
                .id(4L).document("BO004").total(3000).status(false).notificationsDate(LocalDate.now())
                .date(LocalDate.now()).customer(customer).build();
    }
    //Prueba para agregar un ticket al cliente
    @Test
    public void addTicketToCustomer_valid(){
        when(ticketHistoryRepo.findLastCreated()).thenReturn(ticketHistoryThird);

        ticketHistoryService.addTicketToCustomer(customer, 2000);
        ArgumentCaptor<TicketHistory> ticketHistoryCaptor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(ticketHistoryRepo).save(ticketHistoryCaptor.capture());
        TicketHistory ticketHistoryCaptured = ticketHistoryCaptor.getValue();
        assertEquals(ticketHistory.getTotal(), ticketHistoryCaptured.getTotal());
        assertEquals("T-0525-0001", ticketHistoryCaptured.getDocument());
        assertEquals(ticketHistory.isStatus(), ticketHistoryCaptured.isStatus());
        assertEquals(ticketHistory.getDate(), ticketHistoryCaptured.getDate());
        assertEquals(ticketHistory.getCustomer(), ticketHistoryCaptured.getCustomer());
    }

    //Prueba para obtener todos los tickets de un cliente
    @Test
    public void getByCustomerId_validValues(){
        List<TicketHistory> mockTickets =
                Arrays.asList(ticketHistory, ticketHistoryFirst, ticketHistorySecond, ticketHistoryThird);
        when(ticketHistoryRepo.findByCustomerId(1L)).thenReturn(mockTickets);
        List<TicketHistory> expectedTickets = Arrays.asList(ticketHistory, ticketHistoryFirst, ticketHistoryThird);

        List<TicketHistory> result = ticketHistoryService.getByCustomerId(1L);

        verify(ticketHistoryRepo).findByCustomerId(1L);
        assertEquals(3, result.size());
        assertEquals(expectedTickets, result);
        assertEquals(7000, result.stream().mapToInt(TicketHistory::getTotal).sum());
    }

    //Prueba para actualizar el estado de un ticket
    @Test
    public void updateStatus_valid(){
        ticketHistoryService.updateStatus(ticketHistory);
        ArgumentCaptor<TicketHistory> ticketHistoryCaptor = ArgumentCaptor.forClass(TicketHistory.class);

        verify(ticketHistoryRepo).save(ticketHistoryCaptor.capture());

        TicketHistory ticketHistoryCaptured = ticketHistoryCaptor.getValue();
        assertTrue(ticketHistoryCaptured.isStatus());
    }
    @Test
    public void updateStatus_ThrowException(){
        ticketHistory.setStatus(true);

        TicketAlreadyPaidException e = assertThrows(TicketAlreadyPaidException.class,
                () -> ticketHistoryService.updateStatus(ticketHistory));

        verify(ticketHistoryRepo, never()).save(ticketHistory);
        assertEquals("El ticket BO001 ya fue pagado", e.getMessage());
    }

    //Prueba para obtener un ticket por ID
    @Test
    public void getTicketById_valid(){
        when(ticketHistoryRepo.findById(1L)).thenReturn(Optional.of(ticketHistory));

        TicketHistory result = ticketHistoryService.getTicketById(1L);

        verify(ticketHistoryRepo).findById(1L);
        assertEquals(1L, result.getId());
    }
    @Test
    public void getTicketById_invalid(){
        when(ticketHistoryRepo.findById(2L)).thenReturn(Optional.empty());

        TicketNotFoundException e = assertThrows(TicketNotFoundException.class,
                () -> ticketHistoryService.getTicketById(2L));

        assertEquals("Ticket no encontrado", e.getMessage());
    }
}

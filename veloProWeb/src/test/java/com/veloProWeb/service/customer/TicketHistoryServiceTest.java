package com.veloProWeb.service.customer;

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
    @Mock private Customer customer;
    @Mock private CustomerService customerService;
    private TicketHistory ticketHistory;
    private LocalDate lastValidationDate;
    private Customer customerReal;

    @BeforeEach
    void setUp(){
        ticketHistory = new TicketHistory(1L, "BO001", 2000, false, LocalDate.now(), LocalDate.now(), customer);
        customerReal = new Customer();
        customerReal.setId(1L);
    }
    //Prueba para agregar un ticket al cliente
    @Test
    public void AddTicketToCustomer_valid(){
        ticketHistoryService.AddTicketToCustomer(customer, 1L, 2000, LocalDate.now());
        ArgumentCaptor<TicketHistory> ticketHistoryCaptor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(ticketHistoryRepo).save(ticketHistoryCaptor.capture());
        TicketHistory ticketHistoryCaptured = ticketHistoryCaptor.getValue();
        assertEquals(ticketHistory.getTotal(), ticketHistoryCaptured.getTotal());
        assertEquals(ticketHistory.getDocument(), ticketHistoryCaptured.getDocument());
        assertEquals(ticketHistory.isStatus(), ticketHistoryCaptured.isStatus());
        assertEquals(ticketHistory.getDate(), ticketHistoryCaptured.getDate());
        assertEquals(ticketHistory.getCustomer(), ticketHistoryCaptured.getCustomer());
    }

    //Prueba para obtener todos los tickets de un cliente
    @Test
    public void getByCustomerId_valid(){
        ticketHistoryService.getByCustomerId(1L);
        verify(ticketHistoryRepo).findByCustomerId(1L);
    }
    @Test
    public void getByCustomerId_validValues(){
        List<TicketHistory> mockTickets = Arrays.asList(
                ticketHistory,
                new TicketHistory(1L, "BO002", 2000, false, LocalDate.now(), LocalDate.now(), customer),
                new TicketHistory(1L, "BO003", 2000, true, LocalDate.now(), LocalDate.now(), customer),
                new TicketHistory(1L, "BO004", 3000, false, LocalDate.now(), LocalDate.now(), customer));
        when(ticketHistoryRepo.findByCustomerId(1L)).thenReturn(mockTickets);
        List<TicketHistory> expectedTickets = Arrays.asList(
                ticketHistory,
                new TicketHistory(1L, "BO002", 2000, false, LocalDate.now(), LocalDate.now(), customer),
                new TicketHistory(1L, "BO004", 3000, false, LocalDate.now(), LocalDate.now(), customer));
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

    //Prueba para obtener un ticket por id
    @Test
    public void getTicketByID_valid(){
        Long id = 1L;
        when(ticketHistoryRepo.findById(id)).thenReturn(Optional.of(ticketHistory));
        TicketHistory result = ticketHistoryService.getTicketByID(id);

        verify(ticketHistoryRepo).findById(id);
        assertEquals(1L, result.getId());
    }
    @Test
    public void getTicketByID_invalid(){
        Long id = 2L;
        when(ticketHistoryRepo.findById(id)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> ticketHistoryService.getTicketByID(id));

        assertEquals("Ticket no encontrado", e.getMessage());
    }
}

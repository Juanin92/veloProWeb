package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Customer.TicketHistoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
//        lastValidationDate = LocalDate.now();
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

    //Prueba para validar tiempo del ticket de un cliente
    @Test
    public void valideTicketByCustomer_valid(){
        TicketHistoryService spyService = Mockito.spy(ticketHistoryService);
        List<TicketHistory> tickets = Collections.singletonList(ticketHistory);
        when(ticketHistoryRepo.findByCustomerId(customerReal.getId())).thenReturn(tickets);
        doReturn(true).when(spyService).validateDate(ticketHistory);
        doNothing().when(customerService).updateTotalDebt(customerReal);
        spyService.valideTicketByCustomer(customerReal);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService).updateTotalDebt(customerCaptor.capture());
        assertEquals(PaymentStatus.VENCIDA, customerCaptor.getValue().getStatus());
    }
    @Test
    public void valideTicketByCustomer_validReturnFalseMethod(){
        TicketHistoryService spyService = Mockito.spy(ticketHistoryService);
        List<TicketHistory> tickets = Collections.singletonList(ticketHistory);
        when(ticketHistoryRepo.findByCustomerId(customerReal.getId())).thenReturn(tickets);
        doReturn(false).when(spyService).validateDate(ticketHistory);
        spyService.valideTicketByCustomer(customerReal);

        verify(customerService, never()).updateTotalDebt(customerReal);
    }
    @Test
    public void valideTicketByCustomer_validTicketPaid(){
        ticketHistory.setStatus(true);
        TicketHistoryService spyService = Mockito.spy(ticketHistoryService);
        List<TicketHistory> tickets = Collections.singletonList(ticketHistory);
        when(ticketHistoryRepo.findByCustomerId(customerReal.getId())).thenReturn(tickets);
        spyService.valideTicketByCustomer(customerReal);

        verify(customerService, never()).updateTotalDebt(customerReal);
    }
    @Test
    public void valideTicketByCustomer_validLastValidationDateToday() throws NoSuchFieldException, IllegalAccessException {
        TicketHistoryService spyService = Mockito.spy(ticketHistoryService);

        // Configurar lastValidationDate a LocalDate.now() usando reflexión
        Field field = TicketHistoryService.class.getDeclaredField("lastValidationDate");
        field.setAccessible(true);
        field.set(spyService, LocalDate.now());

        spyService.valideTicketByCustomer(customerReal);
        verify(customerService, never()).updateTotalDebt(customerReal);
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

    //Prueba de validación de fecha de un ticket
    @Test
    public void validateDate_validTicketPaid(){
        ticketHistory.setStatus(true);
        ticketHistory.setNotificationsDate(null);
        ticketHistory.setDate(LocalDate.now().minusDays(31));
        boolean result = ticketHistoryService.validateDate(ticketHistory);
        assertFalse(result);
        verify(ticketHistoryRepo, never()).save(ticketHistory);
    }
    @Test
    public void validateDate_validTicketOlderAndNotificationDateNull(){
        ticketHistory.setNotificationsDate(null);
        ticketHistory.setDate(LocalDate.now().minusDays(31));
        boolean result = ticketHistoryService.validateDate(ticketHistory);
        assertTrue(result);
        assertEquals(LocalDate.now(), ticketHistory.getNotificationsDate());
        verify(ticketHistoryRepo).save(ticketHistory);
    }
    @Test
    public void validateDate_validTicketOlderAndNotificationDateOlderThan15Days(){
        ticketHistory.setNotificationsDate(LocalDate.now().minusDays(16));
        ticketHistory.setDate(LocalDate.now().minusDays(31));
        boolean result = ticketHistoryService.validateDate(ticketHistory);
        assertTrue(result);
        assertEquals(LocalDate.now(), ticketHistory.getNotificationsDate());
        verify(ticketHistoryRepo).save(ticketHistory);
    }
    @Test
    public void validateDate_validTicketYoungerThan30Days(){
        ticketHistory.setNotificationsDate(null);
        ticketHistory.setDate(LocalDate.now().minusDays(10));
        boolean result = ticketHistoryService.validateDate(ticketHistory);
        assertFalse(result);
        verify(ticketHistoryRepo, never()).save(ticketHistory);
    }
}

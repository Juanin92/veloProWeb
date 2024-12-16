package com.veloProWeb.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.veloProWeb.Controller.Customer.TicketHistoryController;
import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.DTO.TicketRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Service.Customer.Interfaces.ITicketHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TicketHistoryControllerTest {
    @InjectMocks private TicketHistoryController ticketHistoryController;
    @Mock private ITicketHistoryService ticketHistoryService;
    @Autowired private MockMvc mockMvc;
    private TicketHistory ticketHistory;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer =  new Customer();
        customer.setId(1L);
        mockMvc = MockMvcBuilders.standaloneSetup(ticketHistoryController).setControllerAdvice(new GlobalExceptionHandler()).build();
        ticketHistory = new TicketHistory(1L, "BO001", 2000, false, LocalDate.now(), LocalDate.now(), customer);
    }

    //Prueba de endpoint para obtener los tickets del cliente
    @Test
    public void getListTicketByCustomerData_valid() throws Exception {
        Long customerID = 1L;
        List<TicketHistory> ticketHistoryList = Collections.singletonList(ticketHistory);
        when(ticketHistoryService.getByCustomerId(customerID)).thenReturn(ticketHistoryList);
        mockMvc.perform(get("/Tickets").param("customerId", String.valueOf(customerID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
        verify(ticketHistoryService, times(1)).getByCustomerId(customerID);
    }
    @Test
    public void getListTicketByCustomerData_error() throws Exception {
        Long customerID = 10L;
        when(ticketHistoryService.getByCustomerId(customerID)).thenThrow(new IllegalArgumentException("ID no válido"));
        mockMvc.perform(get("/Tickets").param("customerId", String.valueOf(customerID)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID no válido"));
        verify(ticketHistoryService, times(1)).getByCustomerId(customerID);
    }

    //Pruebas para crear un ticket a un cliente
    @Test
    public void createTicketToCustomer_valid() throws Exception {
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setCustomer(customer);
        dto.setNumber(1L);
        dto.setTotal(2000);
        dto.setDate(LocalDate.now());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String request = mapper.writeValueAsString(dto);
        mockMvc.perform(post("/Tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().string( containsString("Ticket se ha creado para el cliente " + dto.getCustomer().getName())));

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        ArgumentCaptor<Long> numberCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> totalCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(ticketHistoryService, times(1)).AddTicketToCustomer(customerCaptor.capture(),numberCaptor.capture(), totalCaptor.capture(), dateCaptor.capture());
        assertEquals(dto.getCustomer(), customerCaptor.getValue());
        assertEquals(dto.getNumber(), numberCaptor.getValue());
        assertEquals(dto.getTotal(), totalCaptor.getValue());
        assertEquals(dto.getDate(), dateCaptor.getValue());
    }
    @Test
    public void createTicketToCustomer_error() throws Exception {
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setCustomer(customer);
        dto.setNumber(1L);
        dto.setTotal(2000);
        dto.setDate(LocalDate.now());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String request = mapper.writeValueAsString(dto);
        doThrow(new RuntimeException("Por favor, intente más tarde."))
                .when(ticketHistoryService).AddTicketToCustomer(any(Customer.class), any(Long.class), any(Integer.class), any(LocalDate.class));
        mockMvc.perform(post("/Tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado. Por favor, intente más tarde."));
    }

    //Prueba para validar el ticket del cliente
    @Test
    public void valideTicketByCustomer_valid() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String request = mapper.writeValueAsString(customer);
        mockMvc.perform(put("/Tickets/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Tickets validados para el cliente " + customer.getName())));
        verify(ticketHistoryService, times(1)).valideTicketByCustomer(any(Customer.class));
    }
    @Test
    public void valideTicketByCustomer_error() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String request = mapper.writeValueAsString(customer);
        doThrow(new RuntimeException("Por favor, intente más tarde."))
                .when(ticketHistoryService).valideTicketByCustomer(any(Customer.class));
        mockMvc.perform(put("/Tickets/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ocurrió un error inesperado. Por favor, intente más tarde.")));
    }

    //Prueba para actualizar el estado de la deuda de cliente
    @Test
    public void updateStatus_valid() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String request = mapper.writeValueAsString(ticketHistory);
        mockMvc.perform(put("/Tickets/actualizar-estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Actualización del estado del ticket " + ticketHistory.getDocument())));
        verify(ticketHistoryService, times(1)).updateStatus(any(TicketHistory.class));
    }
    @Test
    public void updateStatus_error() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String request = mapper.writeValueAsString(ticketHistory);
        doThrow(new RuntimeException("Por favor, intente más tarde."))
                .when(ticketHistoryService).updateStatus(any(TicketHistory.class));
        mockMvc.perform(put("/Tickets/actualizar-estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ocurrió un error inesperado. Por favor, intente más tarde.")));
    }
}

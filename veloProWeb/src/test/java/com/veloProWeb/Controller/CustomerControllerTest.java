package com.veloProWeb.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Service.Customer.ICustomerService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {
    @InjectMocks private CustomerController customerController;
    @Mock private ICustomerService customerService;
    @Autowired private MockMvc mockMvc;
    private Customer customer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).setControllerAdvice(new GlobalExceptionHandler()).build();
        customer = new Customer(1L, "Juan", "Perez", "+56912345678", "juan.perez@test.com", 100, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
    }

    //Pruebas para obtener una lista de todos los clientes
    @Test
    public void getListCustomerNull_valid() throws Exception {
        when(customerService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(customerService, times(1)).getAll();
    }
    @Test
    public void getListCustomerData_valid() throws Exception {
        List<Customer> customers = Collections.singletonList(
                customer
        );
        when(customerService.getAll()).thenReturn(customers);
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].name").value("Juan"));
        verify(customerService, times(1)).getAll();
    }
    @Test
    public void getListCustomer_error() throws Exception {
        when(customerService.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isInternalServerError())
                        .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(customerService, times(1)).getAll();
    }

    //Pruebas para agregar un nuevo cliente
    @Test
    public void addCustomer_valid() throws Exception{
        mockMvc.perform(post("/clientes/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente agregado correctamente!"));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).addNewCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCustomer.getName());
        assertEquals("Perez", capturedCustomer.getSurname());
    }
    @Test
    public void addCustomer_invalidExistingCustomer() throws Exception {
        doThrow(new IllegalArgumentException("Cliente Existente: Hay registro de este cliente."))
                .when(customerService).addNewCustomer(any(Customer.class));
        mockMvc.perform(post("/clientes/agregar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Cliente Existente: Hay registro de este cliente."));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).addNewCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCustomer.getName());
        assertEquals("Perez", capturedCustomer.getSurname());
    }

    //Pruebas para actualizar un cliente
    @Test
    public void updateCustomer_valid() throws Exception{
        mockMvc.perform(put("/clientes/actualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente actualizado correctamente!"));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCustomer.getName());
        assertEquals("Perez", capturedCustomer.getSurname());
    }
    @Test
    public void updateCustomer_invalidExistingCustomer() throws Exception{
        doThrow(new IllegalArgumentException("Cliente Existente: Hay registro de este cliente."))
                .when(customerService).updateCustomer(any(Customer.class));
        mockMvc.perform(put("/clientes/actualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cliente Existente: Hay registro de este cliente."));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCustomer.getName());
        assertEquals("Perez", capturedCustomer.getSurname());
    }

    //Pruebas para eliminar un cliente
    @Test
    public void deleteCustomer_valid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String customerJson = objectMapper.writeValueAsString(customer);
        doAnswer(invocationOnMock -> {
            Customer capturedCustomer = invocationOnMock.getArgument(0);
            capturedCustomer.setAccount(false);
            return null;
        }).when(customerService).delete(any(Customer.class));
        mockMvc.perform(put("/clientes/eliminar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk());
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).delete(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertFalse(capturedCustomer.isAccount());
    }
    @Test void deleteCustomer_invalid() throws Exception {
        customer.setAccount(false);
        ObjectMapper objectMapper = new ObjectMapper();
        String customerJson = objectMapper.writeValueAsString(customer);
        doThrow(new IllegalArgumentException("Cliente ya ha sido eliminado anteriormente.")).when(customerService).delete(any(Customer.class));
        mockMvc.perform(put("/clientes/eliminar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cliente ya ha sido eliminado anteriormente."));
        verify(customerService, times(1)).delete(any(Customer.class));
    }

    //Pruebas para activar un cliente
    @Test
    public void activeCustomer_valid() throws Exception {
        customer.setAccount(false);
        ObjectMapper objectMapper = new ObjectMapper();
        String customerJson = objectMapper.writeValueAsString(customer);
        doAnswer(invocationOnMock -> {
            Customer capturedCustomer = invocationOnMock.getArgument(0);
            capturedCustomer.setAccount(true);
            return null;
        }).when(customerService).activeCustomer(any(Customer.class));
        mockMvc.perform(put("/clientes/activar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cliente ha sido activado"));
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).activeCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertTrue(capturedCustomer.isAccount());
    }
    @Test void activeCustomer_invalidNull() throws Exception {
        customer.setId(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String customerJson = objectMapper.writeValueAsString(customer);
        doThrow(new IllegalArgumentException("Cliente no válido, Null")).when(customerService).activeCustomer(any(Customer.class));
        mockMvc.perform(put("/clientes/activar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado. Cliente no válido, Null"));
        verify(customerService, times(1)).activeCustomer(any(Customer.class));
    }
    @Test void activeCustomer_invalid() throws Exception {
        customer.setAccount(true);
        ObjectMapper objectMapper = new ObjectMapper();
        String customerJson = objectMapper.writeValueAsString(customer);
        doThrow(new IllegalArgumentException("El cliente tiene su cuenta activada")).when(customerService).activeCustomer(any(Customer.class));
        mockMvc.perform(put("/clientes/activar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado. El cliente tiene su cuenta activada"));
        verify(customerService, times(1)).activeCustomer(any(Customer.class));
    }
}

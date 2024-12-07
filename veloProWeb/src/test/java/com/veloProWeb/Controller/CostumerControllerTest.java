package com.veloProWeb.Controller;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Service.Costumer.ICostumerService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class CostumerControllerTest {
    @InjectMocks private CostumerController costumerController;
    @Mock private ICostumerService costumerService;
    @Autowired private MockMvc mockMvc;
    private Costumer costumer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(costumerController).setControllerAdvice(new GlobalExceptionHandler()).build();
        costumer = new Costumer(1L, "Juan", "Perez", "+56912345678", "juan.perez@test.com", 100, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
    }

    //Pruebas para obtener una lista de todos los clientes
    @Test
    public void getListCostumerNull_valid() throws Exception {
        when(costumerService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(costumerService, times(1)).getAll();
    }
    @Test
    public void getListCostumerData_valid() throws Exception {
        List<Costumer> costumers = Collections.singletonList(
                costumer
        );
        when(costumerService.getAll()).thenReturn(costumers);
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].name").value("Juan"));
        verify(costumerService, times(1)).getAll();
    }
    @Test
    public void getListCostumer_error() throws Exception {
        when(costumerService.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isInternalServerError())
                        .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(costumerService, times(1)).getAll();
    }

    //Pruebas para agregar un nuevo cliente
    @Test
    public void addCostumer_valid() throws Exception{
        mockMvc.perform(post("/clientes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente agregado correctamente!"));

        ArgumentCaptor<Costumer> costumerArgumentCaptor = ArgumentCaptor.forClass(Costumer.class);
        verify(costumerService, times(1)).addNewCostumer(costumerArgumentCaptor.capture());
        Costumer capturedCostumer = costumerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCostumer.getName());
        assertEquals("Perez", capturedCostumer.getSurname());
    }
    @Test
    public void addCostumer_invalidExistingCostumer() throws Exception {
        doThrow(new IllegalArgumentException("Cliente Existente: Hay registro de este cliente."))
                .when(costumerService).addNewCostumer(any(Costumer.class));
        mockMvc.perform(post("/clientes/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Cliente Existente: Hay registro de este cliente."));

        ArgumentCaptor<Costumer> costumerArgumentCaptor = ArgumentCaptor.forClass(Costumer.class);
        verify(costumerService, times(1)).addNewCostumer(costumerArgumentCaptor.capture());
        Costumer capturedCostumer = costumerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCostumer.getName());
        assertEquals("Perez", capturedCostumer.getSurname());
    }

    //Pruebas para actualizar un cliente
    @Test
    public void updateCostumer_valid() throws Exception{
        mockMvc.perform(put("/clientes/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente actualizado correctamente!"));

        ArgumentCaptor<Costumer> costumerArgumentCaptor = ArgumentCaptor.forClass(Costumer.class);
        verify(costumerService, times(1)).updateCostumer(costumerArgumentCaptor.capture());
        Costumer capturedCostumer = costumerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCostumer.getName());
        assertEquals("Perez", capturedCostumer.getSurname());
    }
    @Test
    public void updateCostumer_invalidExistingCostumer() throws Exception{
        doThrow(new IllegalArgumentException("Cliente Existente: Hay registro de este cliente."))
                .when(costumerService).updateCostumer(any(Costumer.class));
        mockMvc.perform(put("/clientes/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Juan\", \"surname\": \"Perez\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cliente Existente: Hay registro de este cliente."));

        ArgumentCaptor<Costumer> costumerArgumentCaptor = ArgumentCaptor.forClass(Costumer.class);
        verify(costumerService, times(1)).updateCostumer(costumerArgumentCaptor.capture());
        Costumer capturedCostumer = costumerArgumentCaptor.getValue();
        assertEquals("Juan", capturedCostumer.getName());
        assertEquals("Perez", capturedCostumer.getSurname());
    }
}

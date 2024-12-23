package com.veloProWeb.Controller.Product;

import com.veloProWeb.Exceptions.GlobalExceptionHandler;
import com.veloProWeb.Model.DTO.ProductDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Product.*;
import com.veloProWeb.Model.Enum.StatusProduct;
import com.veloProWeb.Service.Product.Interfaces.IUnitService;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UnitControllerTest {

    @InjectMocks private UnitController controller;
    @Mock private IUnitService service;
    @Autowired private MockMvc mockMvc;
    private UnitProduct unit;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        unit = new UnitProduct();
        unit.setId(1L);
        unit.setNameUnit("1 KG");
    }

    //Prueba para obtener lista de unidades de medidas registradas
    @Test
    public void getAllUnitsNull_valid() throws Exception {
        when(service.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/unidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllUnitData_valid() throws Exception {
        List<UnitProduct> units = Collections.singletonList(unit);
        when(service.getAll()).thenReturn(units);

        mockMvc.perform(get("/unidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[0].nameUnit").value("1 KG"));
        verify(service, times(1)).getAll();
    }
    @Test
    public void getAllUnit_error() throws Exception {
        when(service.getAll()).thenThrow(new RuntimeException("Ocurrió un error inesperado. Por favor, intente más tarde."));
        mockMvc.perform(get("/unidad"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocurrió un error inesperado. Por favor, intente más tarde."));
        verify(service, times(1)).getAll();
    }

    //Prueba para crear una nueva unidad de medida
    @Test
    public void createUnit_valid() throws Exception {
        mockMvc.perform(post("/unidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"nameUnit\": \"1 KG\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value( "Unidad de medida registrada correctamente"));

        ArgumentCaptor<UnitProduct> unitArgumentCaptor = ArgumentCaptor.forClass(UnitProduct.class);
        verify(service, times(1)).save(unitArgumentCaptor.capture());
        UnitProduct capturedUnit = unitArgumentCaptor.getValue();
        assertEquals(1L, capturedUnit.getId());
        assertEquals("1 KG", capturedUnit.getNameUnit());
    }
    @Test
    public void createUnit_invalidExistingUnit() throws Exception {
        doThrow(new IllegalArgumentException("Unidad ya existe registro"))
                .when(service).save(unit);
        mockMvc.perform(post("/unidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"nameUnit\": \"1 KG\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value( "Unidad ya existe registro"));

        ArgumentCaptor<UnitProduct> unitArgumentCaptor = ArgumentCaptor.forClass(UnitProduct.class);
        verify(service, times(1)).save(unitArgumentCaptor.capture());
        UnitProduct capturedUnit = unitArgumentCaptor.getValue();
        assertEquals(1L, capturedUnit.getId());
        assertEquals("1 KG", capturedUnit.getNameUnit());
    }
}

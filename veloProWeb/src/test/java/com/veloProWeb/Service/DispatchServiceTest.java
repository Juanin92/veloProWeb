package com.veloProWeb.Service;

import com.veloProWeb.Model.DTO.DispatchDTO;
import com.veloProWeb.Model.Entity.Sale.Dispatch;
import com.veloProWeb.Repository.DispatchRepo;
import com.veloProWeb.Service.Sale.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DispatchServiceTest {

    @InjectMocks private DispatchService dispatchService;
    @Mock private DispatchRepo dispatchRepo;
    private Dispatch dispatchDeleted, dispatchRoute, dispatchPreparation, dispatchDelivery;
    private DispatchDTO dispatchRouteDTO, dispatchPreparationDTO, dispatchDeliveryDTO;

    @BeforeEach
    void setUp(){
        dispatchDeleted = new Dispatch(1L, "#1", "Eliminado","calle 1", "Test 1", "Cliente 1", LocalDate.now(),null, new ArrayList<>());
        dispatchPreparation = new Dispatch(2L, "#2", "En Preparación", "calle 2", "Test 2", "Cliente 2", LocalDate.now(),null, new ArrayList<>());
        dispatchRoute = new Dispatch(3L, "#3", "En Ruta","calle 3", "Test 3", "Cliente 3", LocalDate.now(),null, new ArrayList<>());
        dispatchDelivery = new Dispatch(4L, "#4", "Entregado","calle 4", "Test 4", "Cliente 4", LocalDate.now(),LocalDate.now(), new ArrayList<>());

        dispatchPreparationDTO = new DispatchDTO(2L, "#2", "En Preparación", "calle 2", "Test 2", "Cliente 1", LocalDate.now(),null, new ArrayList<>());
        dispatchRouteDTO = new DispatchDTO(3L, "#3", "En Ruta","calle 3", "Test 3", "Cliente 2", LocalDate.now(),null, new ArrayList<>());
        dispatchDeliveryDTO = new DispatchDTO(4L, "#4", "Entregado","calle 4", "Test 4", "Cliente 3", LocalDate.now(),LocalDate.now(), new ArrayList<>());
    }

    //Prueba para obtener los registro de los despachos
    @Test
    public void getDispatches_valid(){
        List<Dispatch> dispatchList = Arrays.asList(dispatchDeleted,dispatchRoute,dispatchPreparation,dispatchDelivery);
        when(dispatchRepo.findAll()).thenReturn(dispatchList);
        List<DispatchDTO> result = dispatchService.getDispatches();
        List<DispatchDTO> dispatchListFiltered = Arrays.asList(dispatchRouteDTO,dispatchPreparationDTO,dispatchDeliveryDTO);

        verify(dispatchRepo, times(1)).findAll();
        assertEquals(dispatchListFiltered, result);
    }

    //Prueba para crear registro de despacho
    @Test
    public void createDispatch_valid(){
        DispatchDTO dto = new DispatchDTO(null, null, null, "Calle Test", "TEST", "Cliente", null, null, new ArrayList<>());
        Dispatch dispatch = dispatchService.createDispatch(dto);
        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals(dispatch.getStatus(), "En Preparación");
        assertEquals(dispatch.getAddress(), "Calle Test");
        assertEquals(dispatch.getComment(), "TEST");
        assertEquals(dispatch.getCreated(), LocalDate.now());
    }
    @Test
    public void createDispatch_validNullDispatch(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> dispatchService.createDispatch(null));
        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("Despacho debe tener datos", e.getMessage());
    }

    //Prueba para manejar los estados de un despacho
    @Test
    public void handleStatus_validRouteDispatch(){
        when(dispatchRepo.findById(2L)).thenReturn(Optional.of(dispatchPreparation));
        dispatchService.handleStatus(2L, 2);

        verify(dispatchRepo, times(1)).save(dispatchPreparation);
        assertEquals("En Ruta", dispatchPreparation.getStatus());
    }
    @ParameterizedTest
    @ValueSource(longs = {2L, 3L})
    public void handleStatus_validDeliveryDispatch(Long id){
        Dispatch dispatch = (id == 2L) ? dispatchPreparation : dispatchRoute;
        when(dispatchRepo.findById(id)).thenReturn(Optional.of(dispatch));
        dispatchService.handleStatus(id, 3);

        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals("Entregado", dispatch.getStatus());
        assertEquals(LocalDate.now(), dispatch.getDeliveryDate());
    }
    @ParameterizedTest
    @ValueSource(longs = {2L, 3L, 4L})
    public void handleStatus_validDeleteDispatch(Long id){
        Dispatch dispatch;
        if (id == 2L) {
            dispatch = dispatchPreparation;
        } else if (id == 3L) {
            dispatch = dispatchRoute;
        } else {
            dispatch = dispatchDelivery;
        }
        when(dispatchRepo.findById(id)).thenReturn(Optional.of(dispatch));
        dispatchService.handleStatus(id, 4);

        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals("Eliminado", dispatch.getStatus());
    }
    @Test
    public void handleStatus_validThrowsExceptionAction() {
        when(dispatchRepo.findById(2L)).thenReturn(Optional.of(dispatchPreparation));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> dispatchService.handleStatus(2L, 6));

        verify(dispatchRepo, never()).save(dispatchPreparation);
        assertEquals("Acción inválida", e.getMessage());
    }
    @Test
    public void handleStatus_validThrowsExceptionNotFoundDispatch() {
        when(dispatchRepo.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> dispatchService.handleStatus(1L, 4));

        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("No se encontró el despacho", e.getMessage());
    }

    //Prueba para obtener un despacho por su identificador
    @Test
    public void getDispatchById_valid(){
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatchDeleted));
        Optional<Dispatch> result = dispatchService.getDispatchById(1L);

        verify(dispatchRepo).findById(1L);
        assertEquals(result.get(), dispatchDeleted);
    }
}

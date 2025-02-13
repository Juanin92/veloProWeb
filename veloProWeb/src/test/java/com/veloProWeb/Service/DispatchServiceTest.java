package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Sale.Dispatch;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Repository.DispatchRepo;
import com.veloProWeb.Service.Sale.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private Sale sale;

    @BeforeEach
    void setUp(){
        sale = new Sale();
        sale.setId(1L);

        dispatchDeleted = new Dispatch(1L, "#1", "Eliminado", LocalDate.now(),null, sale);
        dispatchPreparation = new Dispatch(2L, "#2", "En Preparación", LocalDate.now(),null, sale);
        dispatchRoute = new Dispatch(3L, "#3", "En Ruta", LocalDate.now(),null, sale);
        dispatchDelivery = new Dispatch(4L, "#4", "Entregado", LocalDate.now(),LocalDate.now(), sale);
    }

    //Prueba para obtener los registro de los despachos
    @Test
    public void getDispatches_valid(){
        List<Dispatch> dispatchList = Arrays.asList(dispatchDeleted,dispatchRoute,dispatchPreparation,dispatchDelivery);
        when(dispatchRepo.findAll()).thenReturn(dispatchList);
        List<Dispatch> result = dispatchService.getDispatches();
        List<Dispatch> dispatchListFiltered = Arrays.asList(dispatchRoute,dispatchPreparation,dispatchDelivery);

        verify(dispatchRepo, times(1)).findAll();
        assertEquals(dispatchListFiltered, result);
    }

    //Prueba para crear registro de despacho
    @Test
    public void createDispatch_valid(){
        Dispatch dispatch = new Dispatch(null, null, null, null, null, sale);
        dispatchService.createDispatch(dispatch);
        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals(dispatch.getStatus(), dispatchPreparation.getStatus());
    }
    @Test
    public void createDispatch_validNullDispatch(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> dispatchService.createDispatch(null));
        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("Despacho debe tener datos", e.getMessage());
    }
    @Test
    public void createDispatch_validNullSale(){
        Dispatch dispatch = new Dispatch(null, null, null, null, null, null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> dispatchService.createDispatch(dispatch));
        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("Debe tener una venta asociada", e.getMessage());
    }

    //Prueba para manejar los estados de un despacho
    @Test
    public void handleStatus_validRouteDispatch(){
        Dispatch dispatch = new Dispatch(1L, null, dispatchPreparation.getStatus(), LocalDate.now(), null, sale);
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));
        dispatchService.handleStatus(1L, 2);

        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals("En Ruta", dispatch.getStatus());
    }
    @Test
    public void handleStatus_validDeliveryDispatch(){
        Dispatch dispatch = new Dispatch(1L, null, dispatchRoute.getStatus(), LocalDate.now(), null, sale);
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));
        dispatchService.handleStatus(1L, 3);

        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals("Entregado", dispatch.getStatus());
        assertEquals(LocalDate.now(), dispatch.getDeliveryDate());
    }
    @Test
    public void handleStatus_validDeleteDispatch(){
        Dispatch dispatch = new Dispatch(1L, null, dispatchPreparation.getStatus(), LocalDate.now(), null, sale);
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));
        dispatchService.handleStatus(1L, 4);

        verify(dispatchRepo, times(1)).save(dispatch);
        assertEquals("Eliminado", dispatch.getStatus());
    }
    @Test
    public void handleStatus_validThrowsExceptionAction() {
        Dispatch dispatch = new Dispatch(1L, null, dispatchPreparation.getStatus(), LocalDate.now(), null, sale);
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> dispatchService.handleStatus(1L, 6));

        verify(dispatchRepo, never()).save(dispatch);
        assertEquals("Acción inválida", e.getMessage());
    }
    @Test
    public void handleStatus_validThrowsExceptionNotFoundDispatch() {
        when(dispatchRepo.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> dispatchService.handleStatus(1L, 4));

        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("No se encontró el despacho", e.getMessage());
    }

}

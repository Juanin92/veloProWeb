package com.veloProWeb.service.communication;

import com.veloProWeb.exceptions.communication.AlertNotFoundException;
import com.veloProWeb.exceptions.communication.InvalidAlertActionException;
import com.veloProWeb.model.Enum.AlertStatus;
import com.veloProWeb.model.dto.communication.AlertResponseDTO;
import com.veloProWeb.model.entity.communication.Alert;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.repository.communication.AlertRepo;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @InjectMocks private AlertService alertService;
    @Mock private AlertRepo alertRepo;
    @Mock private UserDetails userDetails;
    @Mock private IRecordService recordService;
    private Product product;

    @BeforeEach
    void setUp(){
        product = Product.builder().id(1L).description("Product test").build();
    }

    //Prueba para obtener todos los registros de alertas
    @Test
    public void getAlerts(){
        Alert alert = Alert.builder().id(1L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.ALERT).product(product).build();
        Alert alert2 = Alert.builder().id(2L).created(LocalDate.now()).description("Alert 2")
                .status(AlertStatus.PENDING).product(product).build();
        when(alertRepo.findByStatusNot(AlertStatus.CHECKED)).thenReturn(List.of(alert, alert2));

        List<AlertResponseDTO> result = alertService.getAlerts();

        verify(alertRepo, times(1)).findByStatusNot(AlertStatus.CHECKED);
        assertEquals(2, result.size());
    }

    //Prueba para crear una alerta
    @Test
    public void createAlert(){
        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        alertService.createAlert(product, "Description test");

        verify(alertRepo, times(1)).save(alertCaptor.capture());

        Alert savedAlert = alertCaptor.getValue();
        assertEquals(LocalDate.now(), savedAlert.getCreated());
        assertEquals(AlertStatus.ALERT, savedAlert.getStatus());
        assertEquals("Description test", savedAlert.getDescription());
        assertEquals(product, savedAlert.getProduct());
    }

    //Prueba para manejar el estado de la alerta
    @Test
    public void updateAlertStatus_validAlertToCheck(){
        Alert alert = Alert.builder().id(1L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.ALERT).product(product).build();
        when(alertRepo.findById(1L)).thenReturn(Optional.of(alert));

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        alertService.updateAlertStatus(1L, AlertStatus.CHECKED, userDetails);

        verify(alertRepo, times(1)).findById(1L);
        verify(alertRepo, times(1)).save(alertCaptor.capture());
        verify(recordService, times(1)).registerAction(userDetails, "UPDATE",
                String.format("Actualizar estado de la alerta %s (%s) ", "Alert 1",
                        AlertStatus.CHECKED));

        Alert resultAlert = alertCaptor.getValue();
        assertEquals(AlertStatus.CHECKED, resultAlert.getStatus());
    }
    @Test
    public void updateAlertStatus_validPendingToCheck(){
        Alert alert = Alert.builder().id(1L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.PENDING).product(product).build();
        when(alertRepo.findById(1L)).thenReturn(Optional.of(alert));

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        alertService.updateAlertStatus(1L, AlertStatus.CHECKED, userDetails);

        verify(alertRepo, times(1)).findById(1L);
        verify(alertRepo, times(1)).save(alertCaptor.capture());
        verify(recordService, times(1)).registerAction(userDetails, "UPDATE",
                String.format("Actualizar estado de la alerta %s (%s) ", "Alert 1",
                        AlertStatus.CHECKED));

        Alert resultAlert = alertCaptor.getValue();
        assertEquals(AlertStatus.CHECKED, resultAlert.getStatus());
    }
    @Test
    public void updateAlertStatus_validAlertToPending(){
        Alert alert = Alert.builder().id(1L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.ALERT).product(product).build();
        when(alertRepo.findById(1L)).thenReturn(Optional.of(alert));

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        alertService.updateAlertStatus(1L, AlertStatus.PENDING, userDetails);

        verify(alertRepo, times(1)).findById(1L);
        verify(alertRepo, times(1)).save(alertCaptor.capture());
        verify(recordService, times(1)).registerAction(userDetails, "UPDATE",
                String.format("Actualizar estado de la alerta %s (%s) ", "Alert 1",
                        AlertStatus.PENDING));

        Alert resultAlert = alertCaptor.getValue();
        assertEquals(AlertStatus.PENDING, resultAlert.getStatus());
    }
    @Test
    public void updateAlertStatus_alertNotFound(){
        when(alertRepo.findById(5L)).thenReturn(Optional.empty());
        AlertNotFoundException e = assertThrows(AlertNotFoundException.class,
                () -> alertService.updateAlertStatus(5L, AlertStatus.CHECKED, userDetails));

        verify(alertRepo, times(1)).findById(5L);
        verify(alertRepo, never()).save(any(Alert.class));
        assertEquals("Alerta no encontrada", e.getMessage());
    }
    @Test
    public void updateAlertStatus_invalidAlertAction(){
        Alert alert = Alert.builder().id(1L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.CHECKED).product(product).build();
        when(alertRepo.findById(1L)).thenReturn(Optional.of(alert));
        InvalidAlertActionException e = assertThrows(InvalidAlertActionException.class,
                () -> alertService.updateAlertStatus(1L, AlertStatus.CHECKED, userDetails));

        verify(alertRepo, times(1)).findById(1L);
        verify(alertRepo, never()).save(any(Alert.class));
        assertEquals("No se puede cambiar de CHECKED a CHECKED", e.getMessage());
    }

    //Prueba para verificar si una alerta activa
    @Test
    public void isAlertActive_validTrue(){
        Alert alert = Alert.builder().id(1L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.ALERT).product(product).build();
        Alert alert2 = Alert.builder().id(2L).created(LocalDate.now()).description("Alert 1")
                .status(AlertStatus.PENDING).product(product).build();
        List<AlertStatus> statusList = List.of(AlertStatus.ALERT, AlertStatus.PENDING);
        when(alertRepo.findByProductAndDescriptionAndStatusIn(product, "Test 1", statusList))
                .thenReturn(List.of(alert, alert2));

        boolean result = alertService.isAlertActive(product, "Test 1");

        verify(alertRepo, times(1)).findByProductAndDescriptionAndStatusIn(product,
                "Test 1", statusList);
        assertTrue(result);
    }
    @Test
    public void isAlertActive_validFalse(){
        List<AlertStatus> statusList = List.of(AlertStatus.ALERT, AlertStatus.PENDING);
        when(alertRepo.findByProductAndDescriptionAndStatusIn(product, "Test 1", statusList))
                .thenReturn(List.of());

        boolean result = alertService.isAlertActive(product, "Test 1");
        verify(alertRepo, times(1)).findByProductAndDescriptionAndStatusIn(product,
                "Test 1", statusList);
        assertFalse(result);
    }
}

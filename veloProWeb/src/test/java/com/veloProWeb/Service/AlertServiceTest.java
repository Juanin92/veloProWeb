package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.User.Alert;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Repository.AlertRepo;
import com.veloProWeb.Service.User.AlertService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @InjectMocks private AlertService alertService;
    @Mock private AlertRepo alertRepo;
    private Alert alert, alert2, alert3, alert4;

    @BeforeEach
    void setUp(){
        alert = new Alert(1L, "Test 1", "Alerta", LocalDate.now(), new Product());
        alert2 = new Alert(2L, "Test 1", "Revisado", LocalDate.now(), new Product());
        alert3 = new Alert(3L, "Test 1", "Revisado", LocalDate.now(), new Product());
        alert4 = new Alert(4L, "Test 1", "Alerta", LocalDate.now(), new Product());
    }

    //Prueba para obtener todos los registros de alertas
    @Test
    public void getAlerts_valid(){
        List<Alert> alerts = Arrays.asList(alert,alert2,alert3,alert4);
        List<Alert> alertListFiltered = Arrays.asList(alert, alert4);
        when(alertRepo.findAll()).thenReturn(alerts);
        List<Alert> result = alertService.getAlerts();

        verify(alertRepo, times(1)).findAll();
        assertEquals(alertListFiltered, result);
    }

    //Prueba para crear una alerta
    @Test
    public void createAlert_valid(){
        Product product = new Product();
        String description = "Test Description";
        alertService.createAlert(product, description);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepo, times(1)).save(alertCaptor.capture());
        Alert savedAlert = alertCaptor.getValue();
        assertEquals(LocalDate.now(), savedAlert.getCreated());
        assertEquals("Alerta", savedAlert.getStatus());
        assertEquals(description, savedAlert.getDescription());
        assertEquals(product, savedAlert.getProduct());
    }

    //Prueba para manejar el estado de la alerta
    @Test
    public void handleAlertStatus_valid(){
        when(alertRepo.findById(1L)).thenReturn(Optional.of(alert));
        alertService.handleAlertStatus(alert, 1);

        verify(alertRepo, times(1)).findById(1L);
        verify(alertRepo, times(1)).save(alert);
        assertEquals("Revisado", alert.getStatus());
    }
    @Test
    public void handleAlertStatus_validNotFoundAlert(){
        Alert nullAlert = new Alert(5L, null, null, LocalDate.now(), null);
        when(alertRepo.findById(5L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> alertService.handleAlertStatus(nullAlert, 1));

        verify(alertRepo, times(1)).findById(5L);
        verify(alertRepo, never()).save(alert);
        assertEquals("Alerta no encontrada", e.getMessage());
    }
    @Test
    public void handleAlertStatus_validAlertCheck(){
        when(alertRepo.findById(2L)).thenReturn(Optional.of(alert2));
        alertService.handleAlertStatus(alert2, 1);

        verify(alertRepo, never()).save(alert);
    }
}

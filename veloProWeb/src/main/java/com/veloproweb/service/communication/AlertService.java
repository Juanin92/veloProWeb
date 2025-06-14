package com.veloproweb.service.communication;

import com.veloproweb.exceptions.communication.AlertNotFoundException;
import com.veloproweb.exceptions.communication.InvalidAlertActionException;
import com.veloproweb.model.Enum.AlertStatus;
import com.veloproweb.model.dto.communication.AlertResponseDTO;
import com.veloproweb.model.entity.communication.Alert;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.repository.communication.AlertRepo;
import com.veloproweb.service.communication.interfaces.IAlertService;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class AlertService implements IAlertService {

    private final AlertRepo alertRepo;
    private final IRecordService recordService;

    /**
     * Obtiene todos los registros de alertas
     * Filtra las alertas que ya se revisaron
     *
     * @return - Lista filtrada con alertas
     */
    @Override
    public List<AlertResponseDTO> getAlerts() {
        List<Alert> alerts = alertRepo.findByStatusNot(AlertStatus.CHECKED);
        return alerts.stream()
                .map(this::mapToAlertResponse)
                .toList();
    }

    /**
     * Crear una alerta.
     * Verifica que el producto no sea nulo
     * @param product - Producto asociado a la alerta
     * @param description - descripción para la alerta
     */
    @Transactional
    @Override
    public void createAlert(Product product, String description) {
        if (product != null) {
            Alert alert =  Alert.builder()
                    .created(LocalDate.now())
                    .status(AlertStatus.ALERT)
                    .description(description)
                    .product(product)
                    .build();
            alertRepo.save(alert);
        }
    }

    /**
     * Manejo del estado de la alerta
     * @param alertId - ID de la alerta seleccionada
     * @param action - Número de la acción a realizar (alerta, revisado, pendiente)
     * @param userDetail - Detalle del usuario autenticado
     */
    @Transactional
    @Override
    public void updateAlertStatus(Long alertId, AlertStatus action, UserDetails userDetail) {
        Alert existingAlert = alertRepo.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alerta no encontrada"));

        AlertStatus currentStatus = existingAlert.getStatus();
        boolean canChange = switch (action) {
            case CHECKED -> currentStatus == AlertStatus.ALERT || currentStatus == AlertStatus.PENDING;
            case PENDING -> currentStatus == AlertStatus.ALERT;
            default -> false;
        };

        if (!canChange) {
            throw new InvalidAlertActionException(
                    String.format("No se puede cambiar de %s a %s", currentStatus.name(), action.name())
            );
        }

        existingAlert.setStatus(action);
        alertRepo.save(existingAlert);
        recordService.registerAction(userDetail, "UPDATE",
                String.format("Actualizar estado de la alerta %s (%s) ", existingAlert.getDescription(),
                        existingAlert.getStatus().name()));
    }

    /**
     * Verifica si hay un registro de ALERTA activo en el registro.
     * Considera alerta activa cuando el estado de la alerta es "Alerta" o "Pendiente"
     * @param product - producto que contiene la alerta
     * @param description - descripción que contiene la alerta
     * @return - Si encuentra registro es TRUE, si está vacío es FALSE
     */
    @Override
    public boolean isAlertActive(Product product, String description) {
        List<AlertStatus> status = Arrays.asList(AlertStatus.ALERT, AlertStatus.PENDING);
        List<Alert> alerts = alertRepo.findByProductAndDescriptionAndStatusIn(product, description, status);
        return !alerts.isEmpty();
    }

    /**
     * Mapea desde la entidad Alert a AlertResponseDTO
     * @param alert - Alerta a mapear
     * @return - DTO de respuesta de alerta
     */
    private AlertResponseDTO mapToAlertResponse(Alert alert){
        return AlertResponseDTO.builder()
                .id(alert.getId())
                .created(alert.getCreated())
                .description(alert.getDescription())
                .status(alert.getStatus())
                .build();
    }
}

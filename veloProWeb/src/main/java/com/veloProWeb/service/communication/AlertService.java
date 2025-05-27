package com.veloProWeb.service.communication;

import com.veloProWeb.model.entity.communication.Alert;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.repository.communication.AlertRepo;
import com.veloProWeb.service.communication.interfaces.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService implements IAlertService {

    @Autowired private AlertRepo alertRepo;
    private final Map<Integer, String> statusMap = new HashMap<>();

    public AlertService(){
        statusMap.put(1, "Alerta");
        statusMap.put(2, "Revisado");
        statusMap.put(3, "Pendiente");
    }

    /**
     * Obtiene todos los registros de alertas
     * Filtra las alertas que ya se revisaron
     * @return - Lista filtrada con alertas
     */
    @Override
    public List<Alert> getAlerts() {
        List<Alert> alerts = alertRepo.findAll();
        return alerts.stream()
                .filter(alert -> !alert.getStatus().equals(statusMap.get(2)))
                .toList();
    }

    /**
     * Crear una alerta.
     * Verifica que el producto no sea nulo
     * @param product - Producto asociado a la alerta
     * @param description - descripción para la alerta
     */
    @Override
    public void createAlert(Product product, String description) {
        if (product != null) {
            Alert alert =  new Alert();
            alert.setId(null);
            alert.setCreated(LocalDate.now());
            alert.setStatus(statusMap.get(1));
            alert.setDescription(description);
            alert.setProduct(product);
            alertRepo.save(alert);
        }
    }

    /**
     * Manejo del estado de la alerta
     * @param alert - Alerta seleccionada
     * @param action - Número de la acción a realizar (alerta, revisado, pendiente)
     */
    @Override
    public void handleAlertStatus(Alert alert, int action) {
        Alert alertExisting = alertRepo.findById(alert.getId()).orElse(null);
        if (alertExisting != null) {
            switch (action){
                case 2:
                    //Verifica la alerta este "Alerta" o "pendiente" para quedar "revisado"
                    if (alertExisting.getStatus().equals(statusMap.get(1)) || alertExisting.getStatus().equals(statusMap.get(3))){
                        alertExisting.setStatus(statusMap.get(2));
                    }
                    break;
                case 3:
                    //Verifica la alerta este "Alerta" y no este "revisado" para quedar "Pendiente"
                    if (alertExisting.getStatus().equals(statusMap.get(1)) && !alertExisting.getStatus().equals(statusMap.get(2))){
                        alertExisting.setStatus(statusMap.get(3));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Acción no valida!");
            }
            alertRepo.save(alertExisting);
        }else{
            throw new IllegalArgumentException("Alerta no encontrada");
        }
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
        List<String> status = Arrays.asList(statusMap.get(1),statusMap.get(3));
        List<Alert> alerts = alertRepo.findByProductAndDescriptionAndStatusIn(product, description, status);
        return !alerts.isEmpty();
    }
}

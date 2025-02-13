package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Alert;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Repository.AlertRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService implements IAlertService{

    @Autowired private AlertRepo alertRepo;
    private final Map<Integer, String> statusMap = new HashMap<>();

    public AlertService(){
        statusMap.put(1, "Alerta");
        statusMap.put(2, "Revisado");
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
     * @param action - Número de la acción a realizar
     */
    @Override
    public void handleAlertStatus(Alert alert, int action) {
        Alert alertExisting = alertRepo.findById(alert.getId()).orElse(null);
        if (alertExisting != null) {
            //Verifica que la alerta no haya si ya revisada
            if (alertExisting.getStatus().equals(statusMap.get(1))){
                alertExisting.setStatus(statusMap.get(2));
                alertRepo.save(alertExisting);
            }
        }else{
            throw new IllegalArgumentException("Alerta no encontrada");
        }
    }
}

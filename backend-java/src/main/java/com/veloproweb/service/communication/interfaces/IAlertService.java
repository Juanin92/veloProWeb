package com.veloproweb.service.communication.interfaces;

import com.veloproweb.model.Enum.AlertStatus;
import com.veloproweb.model.dto.communication.AlertResponseDTO;
import com.veloproweb.model.entity.product.Product;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IAlertService {
    List<AlertResponseDTO> getAlerts();
    void createAlert(Product product, String description);
    void updateAlertStatus(Long alertId, AlertStatus action, UserDetails userDetail);
    boolean isAlertActive(Product product, String description);
}

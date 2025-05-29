package com.veloProWeb.service.communication.interfaces;

import com.veloProWeb.model.Enum.AlertStatus;
import com.veloProWeb.model.dto.communication.AlertResponseDTO;
import com.veloProWeb.model.entity.product.Product;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IAlertService {
    List<AlertResponseDTO> getAlerts();
    void createAlert(Product product, String description);
    void updateAlertStatus(Long alertId, AlertStatus action, UserDetails userDetail);
    boolean isAlertActive(Product product, String description);
}

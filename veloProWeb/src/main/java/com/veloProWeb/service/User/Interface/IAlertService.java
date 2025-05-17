package com.veloProWeb.service.User.Interface;

import com.veloProWeb.model.entity.User.Alert;
import com.veloProWeb.model.entity.product.Product;

import java.util.List;

public interface IAlertService {
    List<Alert> getAlerts();
    void createAlert(Product product, String description);
    void handleAlertStatus(Alert alert, int action);
    boolean isAlertActive(Product product, String description);
}

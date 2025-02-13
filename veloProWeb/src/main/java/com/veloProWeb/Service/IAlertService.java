package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Alert;
import com.veloProWeb.Model.Entity.Product.Product;

import java.util.List;

public interface IAlertService {
    List<Alert> getAlerts();
    void createAlert(Product product, String description);
    void handleAlertStatus(Alert alert, int action);
}

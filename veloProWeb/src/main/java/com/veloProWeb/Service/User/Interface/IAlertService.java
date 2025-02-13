package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.Entity.User.Alert;
import com.veloProWeb.Model.Entity.Product.Product;

import java.util.List;

public interface IAlertService {
    List<Alert> getAlerts();
    void createAlert(Product product, String description);
    void handleAlertStatus(Alert alert, int action);
}

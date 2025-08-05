package com.veloproweb.repository.communication;

import com.veloproweb.model.enums.AlertStatus;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.entity.communication.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepo extends JpaRepository<Alert, Long> {
    List<Alert> findByProductAndDescriptionAndStatusIn(Product product, String description, List<AlertStatus> status);
    List<Alert> findByStatusNot(AlertStatus alertStatus);
}

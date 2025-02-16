package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.User.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepo extends JpaRepository<Alert, Long> {
    List<Alert> findByProductAndDescriptionAndStatusIn(Product product, String description, List<String> status);
}

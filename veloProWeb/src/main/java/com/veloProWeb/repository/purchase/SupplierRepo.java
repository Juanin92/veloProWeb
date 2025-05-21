package com.veloProWeb.repository.purchase;

import com.veloProWeb.model.entity.purchase.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepo extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByRut(String rut);
}
